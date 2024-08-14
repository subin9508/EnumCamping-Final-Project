package com.itwill.finalproject.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.UserCreateDto;
import com.itwill.finalproject.dto.UserDeactivateDto;
import com.itwill.finalproject.dto.UserSignInDto;
import com.itwill.finalproject.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@GetMapping("/signin")
	public void signin() {
		log.info("signin()");
	}

//	@PostMapping("/signin")
//	public String signIn(UserSignInDto dto, @RequestParam(name = "target", defaultValue = "") String target,
//			HttpSession session) throws IOException {
//		log.info("POST signIn({})", dto);
//
//		// 사용자가 존재하는지 확인 (아이디와 비밀번호를 검증)
//		Optional<User> optionalUser = userService.read(dto);
//
//		// 로그인 실패한 경우
//		if (!optionalUser.isPresent()) {
//			// 아이디와 비밀번호가 일치하는 사용자 없는 경우
//
//			return "redirect:/user/signin?result=f&target=" + URLEncoder.encode(target, "UTF-8");
//		}
//
//		User user = optionalUser.get();
//
//		// 비활성화된 사용자 확인
//		log.info("Checking if user is active...");
//		boolean isActive = userService.checkUserIsActive(dto.getUserId());
//		log.info("User active status: {}", isActive);
//		if (!isActive) {
//			// 사용자가 비활성 상태인 경우
//			log.info("User is inactive");
//			return "redirect:/user/signin?result=inactive";
//		}
//
//		// 비활성화 기간 확인
//		if (!userService.checkDeactivationPeriod(dto.getUserId())) {
//			// 비활성화 기간이 남아있는 경우
//			log.info("User is still in deactivation period");
//			return "redirect:/user/signin?result=deactivated";
//		}
//
//		// 로그인 성공 시 세션에 로그인 사용자 아이디를 저장
//		session.setAttribute("signedInUser", user.getUserId());
//		// 세션에 유저 role을 저장
//		log.info("getUserId={}", user.getUserId());
//		session.setAttribute("userRole", user.getUserRole());
//
//		session.setAttribute("loginUserId", user.getUserKey());
//
//		log.info("로그인 성공 - 세션에 loginUserId 저장: {}, 세션에 signedInUser 저장: {}", user.getUserKey(), user.getUserId());
//
//		// 로그인 성공 후 이동할 타겟 페이지
//		String targetPage = (target.equals("")) ? "/" : target;
//
//		return "redirect:" + targetPage;
//
//	}

	@GetMapping("/signout")
	public String signout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
		return "redirect:/";
	}

	@GetMapping("/signup") // GET 방식의 /user/signup 요청을 처리하는 컨트롤러 메서드
	public void signUp() {
		log.info("GET signUp()");
	}

	@PostMapping("/signup") // POST 방식의 /user/signup 요청을 처리하는 컨트롤러 메서드
	public String signUp(UserCreateDto dto) {
		log.info("POST signUp({})", dto);

		userService.create(dto);

		return "redirect:/user/signin"; // 로그인 페이지로 이동.
	}

	// 사용자 아이디 중복체크 REST 컨트롤러
	@GetMapping("/checkid")
	@ResponseBody // 메서드 리턴 값이 클라이언트로 전달되는 데이터.
	public ResponseEntity<String> checkId(@RequestParam(name = "userId") String userId) {
		log.info("checkId(user_id={})", userId);

		boolean result = userService.checkUserid(userId);
		if (result) {
			return ResponseEntity.ok("Y");
		} else {
			return ResponseEntity.ok("N");
		}
	}

	// 사용자 아이디 중복체크 REST 컨트롤러
	@GetMapping("/checkemail")
	@ResponseBody // 메서드 리턴 값이 클라이언트로 전달되는 데이터.
	public ResponseEntity<String> email(@RequestParam(name = "userEmail") String userEmail) {
		log.info("checkEmail(userEmail={})", userEmail);

		boolean result = userService.checkEmail(userEmail);
		if (result) {
			return ResponseEntity.ok("Y");
		} else {
			return ResponseEntity.ok("N");
		}
	}

	@GetMapping("/findid")
	public String findIdForm() {
		return "user/findid"; // 아이디 찾기 입력 폼으로 이동
	}

	@PostMapping("/findid")
	public String findId(@RequestParam("user_name") String name, @RequestParam("user_email") String email,
			Model model) {
		String userId = userService.findIdByNameAndEmail(name, email);
		if (userId != null) {

			model.addAttribute("userId", userId);
			return "user/displayid"; // 아이디 찾기 성공 화면으로 이동
		} else {

			model.addAttribute("message", "등록되지 않은 이름 또는 이메일입니다.");
			return "user/findid"; // 아이디 찾기 입력 폼으로 다시 이동
		}
	}

	@GetMapping("/findpassword")
	public String findPasswordForm(Model model) {
		return "user/findpassword"; // 패스워드 찾기 입력 폼으로 이동
	}

	@PostMapping("/findpassword")
	public String findPassword(@RequestParam("user_name") String name, @RequestParam("user_email") String email,
			@RequestParam("user_id") String id, Model model) {
		String userPassword = userService.findPasswordByNameAndEmailAndId(name, email, id);
		if (userPassword != null) {
			model.addAttribute("userPassword", userPassword);
			return "user/displaypassword"; // 비밀번호 찾기 성공 화면으로 이동
		} else {
			model.addAttribute("message", "등록되지 않은 이름 또는 이메일 또는 아이디 입니다.");
			return "user/findpassword"; // 비밀번호 찾기 입력 폼으로 다시 이동
		}
	}

	// 회원 탈퇴 페이지 조회
	@GetMapping("/deactivateUser")
	public String deactivateAccount(Model model, HttpSession session) {
		// 세션에서 사용자 ID 가져오기
		Integer userKey = (Integer) session.getAttribute("loginUserId");
		log.info("세션에서 가져온 userKey: {}", userKey);
		if (userKey == null) {
			return "redirect:/user/signin"; // 로그인 페이지로 리다이렉트
		}

	      // 사용자 정보 가져오기
        Optional<User> userOptional = userService.findByUserKey(userKey);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.debug("가져온 사용자 정보: {}", user);
            model.addAttribute("user", user);
        } else {
            // 사용자 정보가 없는 경우에 대한 처리
            log.warn("사용자 정보를 찾을 수 없습니다. userKey: {}", userKey);
            return "redirect:/error"; // 예시: 에러 페이지로 리다이렉트
        }

        return "user/deactivateUser";
    }


	// 회원 탈퇴 처리
	@PostMapping("/deactivateUser")
	@ResponseBody
	public ResponseEntity<?> deactivateAccount(@RequestBody UserDeactivateDto dto, HttpSession session,
			HttpServletResponse response) {
		log.info("Received deactivation request for userKey: {}", dto.getUserKey());
		log.info("Password received: {}", dto.getUserPassword());

		// 요청 바디에서 id와 password를 추출
		Integer userKey = (Integer) dto.getUserKey();
		String userPassword = (String) dto.getUserPassword();

		log.info("Before calling service - userKey: {}, password: {}", userKey, userPassword);

		// 회원 탈퇴 서비스 호출
		boolean result = userService.deactivateAccount(userKey, userPassword);

		if (result) {
			// 성공적으로 탈퇴한 경우, 세션 무효화 및 세션 삭제
			session.invalidate();

			// 쿠키 삭제
			Cookie cookie = new Cookie("user", null);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			response.addCookie(cookie);

			log.info("Account deactivated successfully.");
			return ResponseEntity.ok().body("/semiproject");
		} else {
			log.info("비밀번호가 일치하지 않습니다.");
			return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
		}
	}

}