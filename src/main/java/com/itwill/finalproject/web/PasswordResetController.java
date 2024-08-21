package com.itwill.finalproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itwill.finalproject.service.PasswordResetService;

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
    
    
    @GetMapping("/user/displaypassword")
    public String showPasswordResetPage() {
        return "displaypassword";
    }
    

    @PostMapping("/user/findpassword")
    public String resetPassword(@RequestParam("user_email") String email, RedirectAttributes redirectAttributes) {
        try {
            passwordResetService.resetPassword(email);
            redirectAttributes.addFlashAttribute("message", "임시 비밀번호가 이메일로 전송되었습니다.");
            return REDIRECT_SIGNIN;  // 성공 시 로그인 페이지로 리디렉션
        } catch (Exception e) {  // 모든 예외를 한 번에 처리
            logger.error("비밀번호 재설정 중 오류가 발생했습니다: {}", e.getMessage());  // 예외 로그 추가
            redirectAttributes.addFlashAttribute("error", "비밀번호 재설정 중 오류가 발생했습니다: " + e.getMessage());
            return REDIRECT_FIND_PASSWORD;  // 오류 시 비밀번호 찾기 페이지로 리디렉션
        }
    }
}
