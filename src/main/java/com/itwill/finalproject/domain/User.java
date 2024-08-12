package com.itwill.finalproject.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder @Getter
@ToString
@Table(name = "USERS")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userKey; // PK
	
	@Column(nullable = false)
	private String userName; //유저 이름
	
	@Column(nullable = false, unique = true)
	private String userId; // 로그인 아이디
	
	@Column(nullable = false)
	private String userPassword; // 로그인 비밀번호 
	
	@Column(nullable = false, unique = true)
	private String userEmail; //이메일
	
	@Column(nullable = false, unique = true)
	private String userPhone; //핸드폰
	
	@Column(nullable = false)
	private Integer userRole; //일반유저인지 관리자인지
	
	private Integer userState; // 유저 상태 (탈퇴인지 아닌지)
	
	@Column(name = "DEACTIVEUNTIL")
	private LocalDate deactiveuntil;
	
	private String profileImage; // 프로필 사진
}
