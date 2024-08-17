package com.itwill.finalproject.web;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import org.springframework.web.bind.annotation.RequestBody;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.ProfileDto;
import com.itwill.finalproject.dto.QnAListItemDto;
import com.itwill.finalproject.dto.QnAUpdateDto;
import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.dto.UserUpdateDto;
import com.itwill.finalproject.exception.CustomValidationException;
import com.itwill.finalproject.service.MyPageService;
import com.itwill.finalproject.service.ProfileService;
import com.itwill.finalproject.service.QnAService;
import com.itwill.finalproject.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
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
    private final ProfileService profileService;
    
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
        if (user.getUserPassword().equals(password)) {
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
        return "mypage/user_update"; 
    }

    @PostMapping("/user_update")
    @ResponseBody
    public ResponseEntity<?> userUpdate(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "deleteProfileImage", required = false) String deleteProfileImage,
        @ModelAttribute UserUpdateDto dto,
        @AuthenticationPrincipal UserDetails userDetails) {

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

        if (!isValidPassword(dto.getUserPassword())) {
            result.put("success", false);
            result.put("message", "비밀번호는 8자리 이상이며, 영문과 숫자를 포함해야 합니다.");
            return ResponseEntity.badRequest().body(result);
        }

        if (!isValidPhone(dto.getUserPhone())) {
            result.put("success", false);
            result.put("message", "전화번호는 형식에 맞게 입력하세요. 예: 010-1234-5678");
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

            // 프로필 이미지 업로드 처리
            try {
                if (file != null && !file.isEmpty()) {
                    ProfileDto profileDto = new ProfileDto();
                    profileDto.setFile(file);
                    profileDto.setTitle(""); // 빈 문자열로 title 설정
                    profileService.ProfileUpload(profileDto, existingUser);
                }
            } catch (Exception e) {
                log.error("프로필 이미지 업로드 중 오류 발생: " + e.getMessage(), e);
                result.put("success", false);
                result.put("message", "프로필 이미지 업로드에 실패했습니다.");
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

            // 성공 시 리디렉션 URL 포함하여 응답 반환
            result.put("success", true);
            result.put("message", "사용자 정보가 성공적으로 업데이트 되었습니다.");
            result.put("redirectUrl", "/enumcamping/mypage/myInfo?userId=" + dto.getUserId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("사용자 정보 업데이트 중 오류 발생: " + e.getMessage(), e);
            result.put("success", false);
            result.put("message", "사용자 정보 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
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
    
    @GetMapping({ "/qna_details", "/qna_modify" })
    public void details(@RequestParam(name = "id") Long id, Model model) {
        log.info("details(id={})", id);
        
        QnA entity = qnaService.readById(id);
        model.addAttribute("qna", entity);
        
        //-> view 이름은, 요청 주소가 "details"인 경우에는 details.html
        // 요청 주소가 "modify"인 경우에는 modify.html
    }
    
//    @PreAuthorize("hasRole('USER')")
    @GetMapping("/delete")
    public String delete(@RequestParam("id") Long id, @RequestParam(name="userId") String userId, Model model, HttpSession session) {
        log.info("delete(id={})", id);
        
     // 사용자 정보를 조회하여 세선에 저장
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
    
}
