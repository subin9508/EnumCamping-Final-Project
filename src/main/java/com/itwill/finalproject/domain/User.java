package com.itwill.finalproject.domain;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userKey; // PK
	
	private String userName; //유저 이름
	
	private String userId; // 로그인 아이디
	
	private String userPassword; // 로그인 비밀번호 
	
	private String userEmail; //이메일
	
	private String userPhone; //핸드폰
	
	private String userRole; //일반유저인지 관리자인지
	
	private Integer userState; // 유저 상태 (탈퇴인지 아닌지)
	
	private Date deactiveUntil; // 비활성화기간
	
	private String profileImage; // 프로필 사진
}
