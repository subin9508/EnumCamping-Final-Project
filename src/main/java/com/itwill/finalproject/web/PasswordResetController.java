package com.itwill.finalproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itwill.finalproject.service.PasswordResetService;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    @Autowired
    private PasswordResetService passwordResetService;

    // 리다이렉션 경로를 상수로 선언
    private static final String REDIRECT_SIGNIN = "redirect:/user/signin";
    private static final String REDIRECT_FIND_PASSWORD = "redirect:/user/findpassword";
    
    // 비밀번호 재설정 페이지 표시
    @GetMapping("/user/findpassword")
	public String findPasswordForm(Model model) {
		return "user/findpassword"; // 패스워드 찾기 입력 폼으로 이동
	}
    
    // 비밀번호 재설정 요청 처리
    @PostMapping("/user/findpassword")
    public String resetPassword(@RequestParam("user_email") String email, RedirectAttributes redirectAttributes) {
        try {
            passwordResetService.resetPassword(email);
            redirectAttributes.addFlashAttribute("message", "임시 비밀번호가 이메일로 전송되었습니다.");
            return REDIRECT_SIGNIN;  // 성공 시 로그인 페이지로 리디렉션
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 요청: {}", e.getMessage());  // 예외 로그 추가
            redirectAttributes.addFlashAttribute("error", "유효하지 않은 이메일 주소입니다.");
            return REDIRECT_FIND_PASSWORD;  // 오류 시 비밀번호 찾기 페이지로 리디렉션
        } catch (MessagingException e) {
            logger.error("이메일 전송 중 오류가 발생했습니다: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "이메일 전송 중 오류가 발생했습니다.");
            return REDIRECT_FIND_PASSWORD;  // 오류 시 비밀번호 찾기 페이지로 리디렉션
        } 
//        catch (Exception e) {
//            logger.error("비밀번호 재설정 중 오류가 발생했습니다: {}", e.getMessage());
//            redirectAttributes.addFlashAttribute("error", "비밀번호 재설정 중 오류가 발생했습니다.");
//            return REDIRECT_FIND_PASSWORD;  // 오류 시 비밀번호 찾기 페이지로 리디렉션
//        }
    }
}
