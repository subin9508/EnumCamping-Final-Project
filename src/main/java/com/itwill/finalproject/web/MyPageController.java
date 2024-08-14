package com.itwill.finalproject.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import org.springframework.web.bind.annotation.RequestBody;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.QnAListItemDto;
import com.itwill.finalproject.dto.QnAUpdateDto;
import com.itwill.finalproject.dto.UserUpdateDto;
import com.itwill.finalproject.service.MyPageService;
import com.itwill.finalproject.service.QnAService;
import com.itwill.finalproject.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
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
    public String userUpdate(Model model) {
        String userId = getUserId();
        if (userId == null) {
            return "redirect:/user/signin";
        }

        User user = myPageService.read(userId);
        log.debug("authenticated user: {}", user);

        if (user == null) {
            return "redirect:/user/signin";
        }

        model.addAttribute("user", user);
        return "mypage/user_update"; 
    }
    
    @PostMapping("/user_update")
    @ResponseBody
    public ResponseEntity<?> userUpdate(@RequestBody UserUpdateDto dto, HttpServletResponse response) {
        log.debug("user_update(dto={})", dto);

        Map<String, Object> result = new HashMap<>();

        // null 체크
        if (dto == null || dto.getUserId() == null || dto.getUserId().isEmpty()) {
            result.put("success", false);
            result.put("message", "사용자 정보가 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        // 비밀번호 유효성 검사
        if (dto.getUserPassword() != null && !dto.getUserPassword().isEmpty()) {
            if (dto.getUserPassword().length() < 8 || !dto.getUserPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
                result.put("success", false);
                result.put("message", "비밀번호는 8자리 이상이며, 영문과 숫자를 포함해야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
        }

        // 전화번호 유효성 검사
        if (dto.getUserPhone() != null && !dto.getUserPhone().isEmpty()) {
            if (!dto.getUserPhone().matches("^01[0-9]-\\d{3,4}-\\d{4}$")) {
                result.put("success", false);
                result.put("message", "전화번호는 형식에 맞게 입력하세요. 예: 010-1234-5678");
                return ResponseEntity.badRequest().body(response);
            }
        }

        try {
            // 기존 사용자 정보 불러오기
            User existingUser = myPageService.read(dto.getUserId());
            log.info("기존 사용자 정보 = {}", existingUser);

            if (existingUser == null) {
                result.put("success", false);
                result.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 해당 값들이 null일 경우 기존 값 유지
            if(dto.getUserEmail() == null || dto.getUserEmail().isEmpty()) {
                dto.setUserEmail(existingUser.getUserEmail());
            }
            if(dto.getName() == null || dto.getName().isEmpty()) {
                dto.setName(existingUser.getName());
            }
            
            // userRole은 항상 기존 값 사용
            dto.setUserRole(existingUser.getUserRole());
   
            // 사용자 정보 업데이트
            myPageService.update(dto);
            log.info("업데이트 된 정보 = {}", dto);
            
            // 성공 시 리디렉션 URL 포함하여 응답 반환
            result.put("success", true);
            result.put("message", "사용자 정보가 성공적으로 업데이트 되었습니다.");
            result.put("redirectUrl", "/enumcamping/mypage/myInfo?userId=" + dto.getUserId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("사용자 정보 업데이트 중 오류 발생", e);
            result.put("success", false);
            result.put("message", "사용자 정보 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}

    

    // 특정 사용자의 QnA 목록 조회    
    @GetMapping("/qna_list")
	public void qnaList(@RequestParam(name = "p", defaultValue = "0" ) int pageNo, @RequestParam(name="userId") String userId, Model model, HttpSession session) {
		log.debug("qna_list(userId={})", userId);
		
		// 사용자 정보를 조회하여 세선에 저장
		User user = userService.read(userId);
		session.setAttribute("user", user);
		
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

    /*
	// 마이페이지 - 예약목록
	@GetMapping("/reservation_list")
	public String reservationList(@RequestParam(name="userId") String userId, Model model, HttpSession session) {
		log.debug("reservation_list(userId={})", userId);
		//userId로 사용자 정보 찾음(이거 필요 없는 것 같음)
		User user = userService.read(userId);
		session.setAttribute("user", user); // 사용자 정보를 세션에 저장
		
		//userId를 아규먼트로 받으면서 왜 굳이 User 다시 찾고 getUserId를 한거지? 이부분은 기말로 옮기면서 수정하면 될 것 같음
		//user을 list에서 쓰려고 했던 것 같은데 필요 없어서 (주문자 이름 이런거 안 넣음) 안 쓴 것 같음 - 빼면 됨!
		List<ReservationListDto> list = userService.readReservationList(user.getUserId());
		 log.debug("list=({})", list);
	     model.addAttribute("reservations", list);
	     model.addAttribute("user", user); // 모델에 사용자 정보 추가
		
	     return "/user/reservation_list"; // 반환할 뷰의 이름
	}
    
	// 마이페이지 - 예약 상세
    @GetMapping("/reservation_details")
    public void reservationDetails(@RequestParam(name="resId") int resId, Model model) {
    	log.debug("reservation_details()");
    	//예약 번호로 예약 상세 내용들을 받음
    	ReservationMaster resMaster = userService.readReservationMasterDetails(resId);
    	
    	List<ReservationDetailListDto> resDetail = userService.readReservationDetails(resId);
    	
    	//master 내용이랑 detail 내용이 모두 필요함
    	model.addAttribute("resMaster", resMaster);
    	model.addAttribute("resDetail", resDetail);
    	
    }
    */   

    
