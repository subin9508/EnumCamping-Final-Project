package com.itwill.finalproject.dto;


import lombok.Data;

@Data
public class UserSignInDto {

	private String userId; // 사용자 아이디
	private String userPassword; // 사용자 비밀번호
}