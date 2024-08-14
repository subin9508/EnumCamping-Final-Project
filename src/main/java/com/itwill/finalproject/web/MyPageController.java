package com.itwill.finalproject.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.UserUpdateDto;
import com.itwill.finalproject.service.MyPageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    
    private final MyPageService myPageService;
    
    private String getUserIdFromSession(HttpSession session) {
        Object userIdObj = session.getAttribute("signedInUser");
        if (userIdObj instanceof String) {
            return (String) userIdObj;
        }
        log.warn("세션에 유효한 userId가 없습니다.");
        return null;
    }
    
    @GetMapping("/myInfo")
    public String myPage(Model model, HttpSession session) {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            User user = myPageService.read(userId);
            model.addAttribute("user", user);
            log.debug("마이페이지에 표시될 사용자 정보: {}", user);
            return "mypage/myInfo";
        }
        
        log.warn("세션에 로그인된 사용자 정보가 없습니다.");
        return "redirect:/user/signin";
    }
    
    @GetMapping("/password_check")
    public String showPasswordCheckForm() {
        return "mypage/password_check";
    }
    
    @PostMapping("/password_check")
    public String passwordCheck(@RequestParam("password") String password, 
            HttpSession session, Model model) {
        String userId = getUserIdFromSession(session);
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
    public String userUpdate(HttpSession session, Model model) {
        String userId = getUserIdFromSession(session);
        if (userId == null) {
            return "redirect:/user/signin";
        }

        User user = myPageService.read(userId);
        log.debug("session user: {}", user);

        if (user == null) {
            return "redirect:/user/signin";
        }

        model.addAttribute("user", user);
        return "mypage/user_update"; 
    }
    
    @PostMapping("/user_update")
    @ResponseBody
    public ResponseEntity<?> userUpdate(
            @RequestParam("userId") String userId,
            @RequestParam("userPassword") String userPassword,
            @RequestParam("userPhone") String userPhone,
            HttpSession session) {
    	
    	UserUpdateDto dto = new UserUpdateDto();
        dto.setUserId(userId);
        dto.setUserPassword(userPassword);
        dto.setUserPhone(userPhone);
        log.debug("user_update(dto={})", dto);

        Map<String, Object> response = new HashMap<>();

        // 비밀번호 유효성 검사
        if (dto.getUserPassword() != null && !dto.getUserPassword().isEmpty()) {
            if (dto.getUserPassword().length() < 8 || !dto.getUserPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
                response.put("success", false);
                response.put("message", "비밀번호는 8자리 이상이며, 영문과 숫자를 포함해야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
        }

        // 전화번호 유효성 검사
        if (dto.getUserPhone() != null && !dto.getUserPhone().isEmpty()) {
            if (!dto.getUserPhone().matches("^01[0-9]-\\d{3,4}-\\d{4}$")) {
                response.put("success", false);
                response.put("message", "전화번호는 형식에 맞게 입력하세요. 예: 010-1234-5678");
                return ResponseEntity.badRequest().body(response);
            }
        }

        try {
            myPageService.update(dto);
            User updatedUser = myPageService.read(dto.getUserId());
            session.setAttribute("user", updatedUser);

            response.put("success", true);
            response.put("message", "사용자 정보가 성공적으로 업데이트 되었습니다.");
            response.put("redirectUrl", "/mypage/myInfo?userId=" + dto.getUserId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("사용자 정보 업데이트 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "사용자 정보 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
    
    
    
}