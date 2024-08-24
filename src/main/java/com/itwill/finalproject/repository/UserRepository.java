package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

	//이메일 찾기
	 User findByUserEmail(String userEmail);
	
	// 아이디 중복 체크
	Optional<User> findByUserId(String userId);

	// 이메일 중복 체크
	//Optional<User> findByUserEmail(String userEmail);

	// 로그인
	Optional<User> findByUserIdAndUserPassword(String userId, String userPassword);
	
	// name 찾기 
	User findByName(String name);

	// 아이디 찾기
	@Query("SELECT u FROM User u WHERE u.name = :name AND u.userEmail = :userEmail")
	Optional<User> findByNameAndUserEmail(@Param("name") String name, @Param("userEmail") String userEmail);

	// 비밀번호 찾기
//	Optional<User> findByUserNameAndUserEmailAndUserId(@Param("userName") String userName,
//			@Param("userEmail") String userEmail, @Param("userId") String userId);

	// 사용자 ID로 사용자 정보 조회
	Optional<User> findByUserKey(Integer userKey);
	
	 
	
	// 사용자 정보 업데이트
	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.userPassword = :userPassword, u.userPhone = :userPhone WHERE u.userId = :userId")
	int update(@Param("userId") String userId, @Param("userPassword") String userPassword,
			@Param("userPhone") String userPhone);

	// 회원 비활성화
	@Modifying
	@Transactional
	@Query(value = "UPDATE users u " +
	               "SET u.user_role = 2, " +
	               "    u.deactiveuntil = DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY) " +
	               "WHERE u.user_key = :userKey", nativeQuery = true)
	void deactivateUser(@Param("userKey") Integer userKey);
	 
	
	

	// 탈퇴 회원 정보 저장
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO withdraw_users (user_key) VALUES (:userKey)", nativeQuery = true)
	void insertDeletedUser(@Param("userKey") Integer userKey);

	// 비밀번호 확인
	@Query("SELECT COUNT(u) FROM User u WHERE u.userKey = :userKey AND u.userPassword = :userPassword")
	Integer checkPassword(@Param("userKey") Integer userKey, @Param("userPassword") String userPassword);


	// 활성 사용자 확인
	@Query("SELECT COUNT(u) FROM User u WHERE u.userId = :userId AND u.userRole != 2")
	Integer checkUserIsActive(@Param("userId") String userId);


	// 로그인 시 비활성화 기간 확인
	@Query(value = "SELECT COUNT(*) FROM users WHERE user_id = :userId AND (deactiveuntil IS NULL OR deactiveuntil <= CURRENT_DATE)", nativeQuery = true)
	int checkDeactivationPeriod(@Param("userId") String userId);

}
