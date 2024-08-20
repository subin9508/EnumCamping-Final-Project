//package com.itwill.finalproject.config;
//
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import com.itwill.finalproject.service.UserService;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    @Autowired
//    private UserService userService; // UserService는 사용자 정보를 가져오는 서비스입니다.
//
//    public CustomUsernamePasswordAuthenticationFilter(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        // 사용자 ID와 비밀번호를 가져옵니다.
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");
//
//        // 사용자 인증을 시도합니다.
//        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
//        
//        Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
//
//        // 사용자 인증 후 상태를 확인합니다.
//        if (authentication.isAuthenticated()) {
//            // 인증된 사용자 정보를 가져옵니다.
//            String userId = username; // username을 사용하여 사용자 ID를 가져옵니다.
//            Integer userRole = userService.getUserRoleByUserId(userId); // 사용자 상태를 가져옵니다.
//
//            // 사용자 상태가 0 또는 1이 아닌 경우 인증을 실패로 처리합니다.
//            if (userRole == null || (userRole != 0 && userRole != 1)) {
//                throw new BadCredentialsException("User is not active.");
//            }
//        }
//
//        return authentication;
//    }
//}