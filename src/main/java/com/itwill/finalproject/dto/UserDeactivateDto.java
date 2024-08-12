package com.itwill.finalproject.dto;

import lombok.Data;

@Data
public class UserDeactivateDto { //회원 비활성화
	private Integer userKey; // 유저키
	private String userPassword; // 유저비밀번호

}