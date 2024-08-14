package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

	// 아이디 중복 체크
	Optional<User> findByUserId(String userId);

	// 이메일 중복 체크
	Optional<User> findByUserEmail(String userEmail);

	// 로그인
	Optional<User> findByUserIdAndUserPassword(String userId, String userPassword);

	// 아이디 찾기
	Optional<User> findByUserNameAndUserEmail(String userName, String userEmail);

	// 비밀번호 찾기
//	@Query("SELECT u.userPassword FROM User u WHERE u.userName = :userName AND u.userEmail = :userEmail AND u.userId = :userId")
	Optional<User> findByUserNameAndUserEmailAndUserId(@Param("userName") String userName,
			@Param("userEmail") String userEmail, @Param("userId") String userId);

	// 사용자 ID로 사용자 정보 조회
	Optional<User> findByUserKey(Integer userKey);

	// 회원 비활성화
	@Modifying
	@Transactional
	@Query(value = "UPDATE User u SET u.userState = 0, u.deactiveuntil = CURRENT_DATE + INTERVAL '60' DAY WHERE u.userKey = :userKey", nativeQuery = true)
	void deactivateUser(@Param("userKey") Integer userKey);

	// 탈퇴 회원 정보 저장
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO WithdrawUser (userKey) VALUES (:userKey)", nativeQuery = true)
	void insertDeletedUser(@Param("userKey") Integer userKey);

	// 비밀번호 확인
	@Query("SELECT COUNT(u) FROM User u WHERE u.userKey = :userKey AND u.userPassword = :userPassword")
	Integer checkPassword(@Param("userKey") Integer userKey, @Param("userPassword") String userPassword);

	// 활성 사용자 확인
	@Query("SELECT COUNT(u) FROM User u WHERE u.userId = :userId AND u.userState = 1")
	Integer checkUserIsActive(@Param("userId") String userId);

	// 로그인 시 비활성화 기간 확인
	@Query(value = "SELECT COUNT(*) FROM users WHERE user_id = :userId AND (deactiveuntil IS NULL OR deactiveuntil <= CURRENT_DATE)", nativeQuery = true)
	int checkDeactivationPeriod(@Param("userId") String userId);

	

}
