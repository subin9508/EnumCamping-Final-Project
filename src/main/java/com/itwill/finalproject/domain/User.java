package com.itwill.finalproject.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "USERS")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userKey; // PK

	@Column(nullable = false, name = "USER_NAME")
	private String name; // 유저 이름

	@Column(nullable = false, unique = true)
	private String userId; // 로그인 아이디

	@Column(nullable = false)
	private String userPassword; // 로그인 비밀번호

	@Column(nullable = false, unique = true)
	private String userEmail; // 이메일

	@Column(nullable = false)

	private String userPhone; // 핸드폰

//	@Builder.Default// Builder 패턴에서도 null이 아닌 HashSet<> 객체로 초기화될 수 있도록.
	@ToString.Exclude // toString() 메서드에서 제외.
//	@Enumerated(EnumType.STRING) // DB 테이블에 저장될 때 상수(enum) 이름(문자열)을 사용.
	private Integer userRole;
	/*
	 * @Column(nullable = false) private String userRole; //일반유저인지 관리자인지
	 */

	@Column(columnDefinition = "INT DEFAULT 1")
	private Integer userState; // 유저 상태 (탈퇴인지 아닌지)

	@Column(name = "DEACTIVEUNTIL")
	private LocalDate deactiveuntil;

	// 편의 메서드
//	// 유저의 권한을 부여하는 메서드.
//	public User addRole(UserRole role) {
//		Role.add(role);
//		return this;
//	}
//
//	// 유저의 권한을 한 개 삭제하는 메서드
//	public User removeRole(UserRole role) {
//		Role.remove(role);
//		return this;
//	}
//
//	// 유저의 권한을 삭제하는 메서드
//	public User clearRoles() {
//		Role.clear(); // Set<>이 가지고 있는 모든 원소를 지움.
//		return this;
//	}

//	// 사용자 권한을 문자열로 변환
//	public String getRoleString(Integer role) {
//		switch (role) {
//		case 0:
//			return "ADMIN";
//		case 1:
//			return "USER";
//		default:
//			return "ROLE_UNKNOWN";
//		}
//	}


	// 사용자 권한을 문자열로 변환
	public String getRoleString(Integer role) {
	    if (role == 0) {
	        return UserRole.ADMIN.getAuthority(); // "ADMIN"
	    } else if (role == 1) {
	        return UserRole.USER.getAuthority(); // "USER"
	    } else {
	        return "ROLE_UNKNOWN";
	    }
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    // userRole 값에 따라 권한 문자열을 생성
	    String roleString = getRoleString(this.userRole);
	    return List.of(new SimpleGrantedAuthority("ROLE_" + roleString));
	}
	
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//	    // userRole 값에 따라 권한 문자열을 생성
//	    String roleString = getRoleString(this.userRole);
//	    return List.of(new SimpleGrantedAuthority(roleString));
//	}
	
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		// 권한 숫자를 문자열로 변환 후 권한 객체 생성
//		return List.of(new SimpleGrantedAuthority(getRoleString(this.userRole)));
//	}

//	 @Override
//	    public Collection<? extends GrantedAuthority> getAuthorities() {
//	        List<String> roleAuthorities = UserRole.getAllAuthorities();
//
//	        List<SimpleGrantedAuthority> authorities = roleAuthorities.stream()
//	                .map((r) -> new SimpleGrantedAuthority(r))
//	                .toList();
//	        
//	        return authorities;
//	    }
	

	@Override
	public String getPassword() {

		return userPassword;
	}

	@Override
	public String getUsername() {
		
		return userId;
	}


}
