package com.itwill.finalproject.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.itwill.finalproject.domain.User;

import lombok.Data;

// 회원 가입 요청에서 요청 파라미터들을 저장하기 위한 DTO
@Data
public class UserCreateDto {
	private String userId; // 사용자 아이디
	private String userPassword; // 사용자 비밀번호
	private String userName; // 사용자 이름
	private String userPhone; // 사용자 전화번호
	private String userEmail; // 사용자 이메일
	private Integer userRole = 0; // 유저롤
	private Integer userState = 1; // 유저상태
	

	// UserCreateDto 객체를 User 엔티티 객체로 변환
	public User toEntity(PasswordEncoder encoder) {
		return User.builder().userId(userId).userPassword(encoder.encode(userPassword)).userName(userName).userPhone(userPhone)
				.userEmail(userEmail).userState(userState).userRole(userRole).build();
	}

}