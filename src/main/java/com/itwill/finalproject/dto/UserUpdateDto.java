package com.itwill.finalproject.dto;

import com.itwill.finalproject.domain.User;

import lombok.Data;

@Data
public class UserUpdateDto {
	private String userId;
	private String userPassword;
	private String userPhone;
	
	public User toEntity() {
		return User.builder()
				.userId(userId)
				.userPassword(userPassword)
				.userPhone(userPhone)
				.build();
	}
}
