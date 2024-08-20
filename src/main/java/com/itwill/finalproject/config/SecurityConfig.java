package com.itwill.finalproject.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.itwill.finalproject.domain.UserRole;
import com.itwill.finalproject.exception.UserAccountDeactivatedException;
import com.itwill.finalproject.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration

//-> 스프링 컨테이너에서 생성하고 관리하는 설정 컴포넌트.
//-> 스프링 컨테이너에서 필요한 곳에 의존성 주입을 해줌.
//@EnableMethodSecurity
@EnableMethodSecurity(prePostEnabled = true)
//-> 컨트롤러 메서드에서 인증(로그인), 권한 설정을 하기 위해서.
public class SecurityConfig {

	// Spring Security 5 버전부터 비밀번호는 반드시 암호화를 해야만 함.
	// 만약 비밀번호를 암호화하지 않으면, HTTP 403(access denied, 접근 거부) 또는
	// HTTP 500(internal server error, 내부 서버 오류) 에러가 발새함.
	// 비밀번호를 암호화하는 객체를 스프링 컨테이너가 bean으로 관리해야 함.
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private final UserService userService;

	@Autowired
	public SecurityConfig(@Lazy UserService userService) {
		this.userService = userService;
	}

	 @Bean
	 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		    http.csrf(csrf -> csrf.disable())
		        .formLogin(formLogin -> formLogin
		            .loginPage("/user/signin")
		            .loginProcessingUrl("/user/signin")
		            .defaultSuccessUrl("/", true)
		            .failureHandler(authenticationFailureHandler())  // 커스텀 실패 핸들러 추가
		            .permitAll())
		        .logout(logout -> logout.permitAll())
		        .authorizeHttpRequests(auth -> auth
		            .requestMatchers("/user/signin").permitAll()
		            .anyRequest().permitAll())
		        .userDetailsService(userService);

		    return http.build();
		}

	    // 추가: AuthenticationProvider 설정
//	    @Bean
//	    public AuthenticationProvider authenticationProvider() {
//	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//	        authProvider.setUserDetailsService(userService);
//	        authProvider.setPasswordEncoder(passwordEncoder());
//	        return authProvider;
//	    }
	 
	 @Bean
	 public AuthenticationFailureHandler authenticationFailureHandler() {
	     return new SimpleUrlAuthenticationFailureHandler() {
	         @Override
	         public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                             AuthenticationException exception) throws IOException, ServletException {
	             if (exception instanceof UserAccountDeactivatedException) {
	                 getRedirectStrategy().sendRedirect(request, response, "/user/signin");
	             } else {
	                 super.onAuthenticationFailure(request, response, exception);
	             }
	         }
	     };
	 }
}