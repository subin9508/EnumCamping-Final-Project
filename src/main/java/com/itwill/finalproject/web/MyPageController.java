package com.itwill.finalproject.web;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.Profile;
import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.domain.QnAAnswers;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.ProfileDto;
import com.itwill.finalproject.dto.QnAListItemDto;
import com.itwill.finalproject.dto.QnAUpdateDto;
import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.dto.UserUpdateDto;
import com.itwill.finalproject.repository.ProfileRepository;
import com.itwill.finalproject.service.MyPageService;
import com.itwill.finalproject.service.ProfileService;
import com.itwill.finalproject.service.QnAAnswerService;
import com.itwill.finalproject.service.QnAService;
import com.itwill.finalproject.service.ReservationService;
import com.itwill.finalproject.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    
    private final MyPageService myPageService;
    private final UserService userService;
    private final QnAService qnaService;
    private final QnAAnswerService qnaanwserSvc;
    private final ProfileService profileService;
    private final ReservationService reservationSvc;
    private final ProfileRepository profileRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    @GetMapping("/myInfo")
    public String myPage(Model model) {
        String userId = getUserId();
        if (userId != null) {
            User user = myPageService.read(userId);
            model.addAttribute("user", user);
            if (user.getProfile() != null && user.getProfile().getProfileImageUrl() != null) {
                model.addAttribute("profileImageUrl", "/profile/" + user.getProfile().getProfileImageUrl());
            }
            log.debug("마이페이지에 표시될 사용자 정보: {}", user);
            return "mypage/myInfo";
        }
        
        log.warn("인증된 사용자 정보가 없습니다.");
        return "redirect:/user/signin";
    }
    
    @GetMapping("/password_check")
    public String showPasswordCheckForm() {
        return "mypage/password_check";
    }
    
    @PostMapping("/password_check")
    public String passwordCheck(@RequestParam("password") String password, Model model) {
        String userId = getUserId();
        if (userId == null) {
            return "redirect:/user/signin";
        }
        
        User user = myPageService.read(userId);
        if (user == null) {
            return "redirect:/user/signin";
        }
        
        // 비밀번호 확인 로직 (서비스 레이어에서 처리하도록 수정 필요)
        if (passwordEncoder.matches(password, user.getUserPassword())) {
            return "redirect:/mypage/user_update";
        } else {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "mypage/password_check";
        }
    }
    
    @GetMapping("/user_update")

    public String userUpdate(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        if (userId == null) {
            return "redirect:/user/signin";
        }

        User user = myPageService.read(userId);
        if (user == null) {
            return "redirect:/user/signin";
        }

        model.addAttribute("user", user);
        model.addAttribute("oldPassword", user.getUserPassword()); // 기존 비밀번호 전달
        return "mypage/user_update"; 
    }

    @PostMapping("/user_update")
    @ResponseBody
    public ResponseEntity<?> userUpdate(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "deleteProfileImage", required = false) String deleteProfileImage,
        @ModelAttribute UserUpdateDto dto,
        @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {

        Map<String, Object> result = new HashMap<>();

        // 사용자 ID 설정
        String userId = userDetails.getUsername();
        dto.setUserId(userId);

        // 유효성 검사
        if (dto == null || dto.getUserId() == null || dto.getUserId().isEmpty()) {
            result.put("success", false);
            result.put("message", "사용자 정보가 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            // 기존 사용자 정보 불러오기
            User existingUser = myPageService.read(dto.getUserId());
            if (existingUser == null) {
                result.put("success", false);
                result.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(result);
            }

            // 비밀번호 처리
            if (dto.getUserPassword() != null && !dto.getUserPassword().trim().isEmpty()) {
                if (!isValidPassword(dto.getUserPassword())) {
                    result.put("success", false);
                    result.put("message", "비밀번호는 8자리 이상이며, 영문과 숫자를 포함해야 합니다.");
                    return ResponseEntity.badRequest().body(result);
                }
                
                // 새 비밀번호가 기존 비밀번호와 동일한지 확인
                if (passwordEncoder.matches(dto.getUserPassword(), existingUser.getUserPassword())) {
                	result.put("success", false);
                	result.put("message", "새 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다.");
                	return ResponseEntity.badRequest().body(result);
                }
                 
                // 새 비밀번호 암호화
                dto.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));
            } else {
                // 비밀번호가 비어있으면 기존 비밀번호 유지
                dto.setUserPassword(existingUser.getUserPassword());
            }

            if (!isValidPhone(dto.getUserPhone())) {
                result.put("success", false);
                result.put("message", "전화번호는 형식에 맞게 입력하세요. 예: 010-1234-5678");
                return ResponseEntity.badRequest().body(result);
            }

            // 프로필 이미지 업로드 처리
            try {
                if (file != null && !file.isEmpty()) {
                    ProfileDto profileDto = new ProfileDto();
                    profileDto.setFile(file);
                    profileDto.setTitle(""); // 빈 문자열로 title 설정
                    String imageFileName = profileService.ProfileUpload(profileDto, existingUser);
                    existingUser.setProfile(profileRepo.findByUser(existingUser).orElse(new Profile()));
                    
                    // 성공적으로 업로드된 경우 처리
                    log.info("프로필 이미지 업로드 성공: {}", imageFileName);                
                  }
            } catch (Exception e) {
                log.error("프로필 이미지 업로드 중 오류 발생: " + e.getMessage(), e);
                result.put("success", false);
                result.put("message", "프로필 이미지 업로드에 실패했습니다: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }

            // 프로필 이미지 삭제 처리
            try {
                if ("true".equals(deleteProfileImage)) {
                    profileService.deleteProfileImage(existingUser);
                }
            } catch (Exception e) {
                log.error("프로필 이미지 삭제 중 오류 발생: " + e.getMessage(), e);
                result.put("success", false);
                result.put("message", "프로필 이미지 삭제에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }

            // 사용자 정보 업데이트
            myPageService.update(dto);

         // 성공 시 JSON 문자열로 직접 변환하여 반환
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("message", "사용자 정보가 성공적으로 업데이트 되었습니다.");
            responseMap.put("redirectUrl", "/enumcamping/mypage/myInfo?userId=" + dto.getUserId());
            String jsonResponse = objectMapper.writeValueAsString(responseMap);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonResponse);
        } catch (Exception e) {
            log.error("사용자 정보 업데이트 중 오류 발생: " + e.getMessage(), e);
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("success", false);
            errorMap.put("message", "사용자 정보 업데이트에 실패했습니다: " + e.getMessage());
            String jsonError = objectMapper.writeValueAsString(errorMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(jsonError);
        }
    }

    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^01[0-9]-\\d{3,4}-\\d{4}$");
    }


    


    // 특정 사용자의 QnA 목록 조회    
    @GetMapping("/qna_list")
	public void qnaList(@RequestParam(name = "p", defaultValue = "0" ) int pageNo, Model model) {
		log.debug("qna_list(pageNo={})", pageNo);
		
		// 사용자 정보를 조회하여 세선에 저장
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // 인증된 사용자의 이름(ID)
		User user = userService.read(userId);
		
		// 해당 사용자의 QnA 목록 조회
		 Page<QnAListItemDto> page = qnaService.readByUserId(userId, pageNo, Sort.by("id").descending());
		 log.debug("page=({})", page);
	     model.addAttribute("pager", page);
	     model.addAttribute("user", user); // 모델에 사용자 정보 추가
	     model.addAttribute("qnas", page.getContent());
	     model.addAttribute("totalCount", page.getTotalElements());
	     
	     // 현재 페이지 번호, 총 페이지 수를 모델에 추가
		 model.addAttribute("currentPage", page.getNumber()); // 현재 페이지 번호 (0부터 시작)
		 model.addAttribute("totalPages", page.getTotalPages()); // 총 페이지 수
	     
	    // pagination fragment에서 사용할 현재 요청 주소 정보
	    model.addAttribute("baseUrl", "/mypage/qna_list");
	}
    
 // QnA 게시글 수정 폼 조회
    @GetMapping("/qna_modify")
    public String modifyForm(@RequestParam(name = "id") Long id, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model, RedirectAttributes redirectAttributes) {
        log.debug("modifyForm(Id={})", id);

        // QnA 게시글 조회
        QnA qna = qnaService.readById(id);
        String signedInUser = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                                     .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        log.debug("signedInUser: {}", signedInUser);
        log.debug("isAdmin: {}", isAdmin);
        log.debug("qnaUserId: {}", qna.getQnaUserId());

//        // 비밀글 여부 확인
//        if (qna.getQnaLock() == 1 && !qna.getQnaUserId().equals(signedInUser) && !isAdmin) {
//            redirectAttributes.addFlashAttribute("message", "비밀글은 작성자와 관리자만 볼 수 있습니다.");
//            return "redirect:/community/qna/list"; // 접근 거부 시 리스트 페이지로 리다이렉트
//        }

        model.addAttribute("qna", qna);
        model.addAttribute("signedInUser", signedInUser);
        model.addAttribute("userRole", isAdmin ? 0 : 1);

        return "/mypage/qna_modify"; // 수정 페이지로 이동
    }

    // QnA 게시글 상세 조회
    @GetMapping("/qna_details")
    public String details(@RequestParam(name = "id") Long id,
    					  @RequestParam(name = "p", defaultValue = "0") int pageNo,
                          @AuthenticationPrincipal  UserDetails userDetails,
                          Model model, RedirectAttributes redirectAttributes) {
    	log.debug("details(Id={}, pageNo={})", id, pageNo);
    	
        // 현재 인증된 사용자 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        String signedInUser = null;
        Integer userRole = null;

        if (principal instanceof UserDetails) {
            UserDetails authenticatedUserDetails = (UserDetails) principal;
            signedInUser = authenticatedUserDetails.getUsername();
            userRole = userService.findByUserId(signedInUser).getUserRole();
        } else if (principal instanceof String) {
            signedInUser = (String) principal;
            User user = userService.findByUserId(signedInUser);
            if (user != null) {
                userRole = user.getUserRole();
            }
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }
        
     
//        // 현재 인증된 사용자 정보를 가져옴
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails authenticatedUserDetails = (UserDetails) authentication.getPrincipal();
        
//        // UserService를 통해 User 객체를 가져옴
//        User user = userService.findByUserId(authenticatedUserDetails.getUsername());
//        
//        // 유저가 null인 경우 예외 처리
//        if (user == null) {
//            throw new NullPointerException("User object is null");
//        }
//        
//        model.addAttribute("user", user);
        
        // QnA 게시글 조회
        QnA qna = qnaService.readById(id);

        // 로그인을 하지 않은 경우, userDetails는 null
//        String signedInUser = (user1 != null) ? user1.getUsername() : null;
//        Integer userRole = null;
//        
//        if (user1!= null) {
//            userRole = ((User) user1).getUserRole(); // userRole 값을 가져옴
//        }

        // 로그인을 하지 않은 경우, userDetails는 null
//        String signedInUser = user.getUsername();
//        Integer userRole = user.getUserRole();
        
        log.debug("signedInUser: {}", signedInUser);
        log.debug("userRole: {}", userRole);
        log.debug("qnaUserId: {}", qna.getQnaUserId());
        log.debug("qnaLock: {}", qna.getQnaLock());

        // 비밀글 여부 확인
//        if (qna.getQnaLock() == 1 && !qna.getQnaUserId().equals(signedInUser) && !isAdmin) {
//            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            return "redirect:/community/qna/list"; // 접근 거부 시 리스트 페이지로 리다이렉트
//        }

//        if (!canAccessQnA(qna, userDetails)) {
//            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            return "redirect:/community/qna/list?p=" + pageNo;
//        }
        
        // 비밀글 여부 확인
        if (qna.isSecret()) {
            // 비밀글인데 로그인하지 않았거나 작성자가 아니거나 관리자가 아닌 경우 접근 불가
            if (signedInUser == null || 
                (!qna.getQnaUserId().equals(signedInUser) && userRole != 0)) {
                redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
                return "redirect:/mypage/qna_list";
            }
        }
        
     // 조회수 증가 조건: 비밀글이 아닌 경우엔 모든 회원 및 비회원 및 관리자 증가 / 비밀글인 경우 작성자와 관리자만 증가
//        if (!qna.isSecret() || qna.getQnaUserId().equals(signedInUser)) {
//        	log.debug("Incrementing view count");
//        	qna.incrementViewCount(); // 조회수 증가 메서드 호출
//        }

//        if (!qna.isSecret()) {
//            // 비밀글이 아니면 모든 사용자에게 조회수 증가
//            log.debug("Incrementing view count for non-secret QnA");
//            qna.incrementViewCount();
//        } else if (signedInUser != null && (qna.getQnaUserId().equals(signedInUser) || userRole == 0)) {
//            // 비밀글인데 작성자이거나 관리자일 경우에만 조회수 증가
//            log.debug("Incrementing view count for secret QnA (authorized user)");
//            qna.incrementViewCount();
//        } else {
//            // 비밀글인데 작성자나 관리자가 아닌 경우 접근 불가
//            redirectAttributes.addFlashAttribute("message", "작성자와 관리자만 접근 가능합니다.");
//            return "redirect:/community/qna/list";
//        }
        
        // 비밀글 여부 확인 및 조회수 증가
        qna = qnaService.incrementViewCount(id, signedInUser, userRole);
        
        // 댓글 목록 조회
        List<QnAAnswers> comments = qnaanwserSvc.readCommentsList(id);
        model.addAttribute("comments", comments);
        log.debug("Comments: {}", comments);
        
        // **답변 목록 조회 추가**
        List<QnAAnswers> qnaAnswers = qnaanwserSvc.findByQnaId(id);  // QnA ID로 답변 리스트 조회
        model.addAttribute("qnaAnswers", qnaAnswers);  // 답변 리스트 모델에 추가
        
        // 답변이 없으면 상태를 '답변 대기'로 설정
        if (qnaAnswers.isEmpty()) {
            qna.setQnaState(0);  // 답변 대기 상태
        } else {
            qna.setQnaState(1);  // 답변 완료 상태
        }
        
        
        model.addAttribute("qna", qna);
        model.addAttribute("signedInUser", signedInUser);
        model.addAttribute("userRole", userRole);
        model.addAttribute("pageNo", pageNo);

        return "/mypage/qna_details";
    }
    
//    @PreAuthorize("hasRole('USER')")
	@GetMapping("/delete")
	public String delete(@RequestParam("id") Long id, Model model, HttpSession session) {
		log.info("delete(id={})", id);

		// 사용자 정보를 조회하여 세선에 저장
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName(); // 인증된 사용자의 이름(ID)
		User user = userService.read(userId);
		session.setAttribute("user", user);

		qnaService.delete(id);

		return "redirect:/mypage/qna_list?userId=" + userId;
	}
    
    @PostMapping("/update")
    public String update(QnAUpdateDto dto) {
        log.info("update(dto={})", dto);
        
        qnaService.update(dto);
        
        return "redirect:/mypage/qna_details?id=" + dto.getId();
    }


	// 마이페이지 - 예약목록
	@GetMapping("/reservation_list")
	public String reservationList(@RequestParam(name="userId") String userId, Model model) {
		log.debug("reservation_list(userId={})", userId);
		
 		List<ReservationMaster> list = myPageService.readAllReservation(userId);
		log.debug("list=({})", list);
	    model.addAttribute("reservations", list);
	    return "/mypage/reservation_list"; // 반환할 뷰의 이름
	}
    
	// 마이페이지 - 예약 상세
    @GetMapping("/reservation_details")
    public void reservationDetails(@RequestParam(name="resId") int resId, Model model) {
    	log.debug("reservation_details()");
    	//예약 번호로 예약 상세 내용들을 받음
    	Optional<ReservationMaster> resMaster = myPageService.readReservationMasterDetails(resId);
    	log.info("resMaster={}",resMaster);
    	List<ReservationDetailDto> resDetail = myPageService.readReservationDetails(resId);
    	log.info("resDetail = {}",resDetail);
    	//master 내용이랑 detail 내용이 모두 필요함
    	model.addAttribute("resMaster", resMaster.get());
    	model.addAttribute("resDetail", resDetail);
    }
    
    // 마이페이지 - 예약 변경
    @GetMapping("/reservation_update")
	public void reservationUpdateCalendar(@RequestParam(name="resId") int resId, Model model) {
		log.info("reservationUpdateCalendar");
		List<Items> items = reservationSvc.getAllItems();
		Optional<ReservationMaster> resMaster = myPageService.readReservationMasterDetails(resId);
    	log.info("resMaster={}",resMaster);
		
		for (Items item : items) {
			log.info("Item: {}", item);
		}
		model.addAttribute("items", items);
		model.addAttribute("resMaster", resMaster.get());
	}
    
    @GetMapping("/reservation_update/{date}")
	@ResponseBody
	public List<Integer> reservationUpdateCalendar(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		log.debug("GET: calendar with date {}", date);
		
		 // 해당 날짜에 예약된 구역 ID 목록을 가져옵니다.
        List<Integer> reservedAreaIds = reservationSvc.readReservedAreas(date);
        return reservedAreaIds;
	}
    
    @GetMapping("/reservation_update/{date}/{area}")
	public ResponseEntity<List<ReservationMaster>> reservationUpdateCalendar(@PathVariable("date") String date, @PathVariable("area") int area) {
		LocalDate checkInDate = LocalDate.parse(date);
		log.debug("GET: calendar with date and area {}, {}", date, area);
		List<ReservationMaster> reservations = reservationSvc.readReservationMaster(checkInDate, area);
		
		return new ResponseEntity<>(reservations, HttpStatus.OK);
	}
    
}
