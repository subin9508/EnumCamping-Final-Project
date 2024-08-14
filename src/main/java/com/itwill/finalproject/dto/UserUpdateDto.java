package com.itwill.finalproject.dto;

import com.itwill.finalproject.domain.User;
import lombok.Data;

@Data
public class UserUpdateDto {
	private String userId;
	private String userPassword;
	private String userPhone;
	private String userEmail;
	private String name;
	private Integer userRole;
	
	public User toEntity() {
		return User.builder()
				.userId(userId)
				.userPassword(userPassword)
				.userPhone(userPhone)
				.userEmail(userEmail)
				.name(name)
				.userRole(userRole)
				.build();
	}
}
