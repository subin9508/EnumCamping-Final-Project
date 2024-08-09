package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    @Query("SELECT u.userId FROM User u WHERE u.userName = :userName AND u.userEmail = :userEmail")
    Optional<String> findIdByNameAndEmail(@Param("userName") String userName, @Param("userEmail") String userEmail);

    // 비밀번호 찾기
    @Query("SELECT u.userPassword FROM User u WHERE u.userName = :userName AND u.userEmail = :userEmail AND u.userId = :userId")
    Optional<String> findPasswordByNameAndEmailAndId(@Param("userName") String userName, @Param("userEmail") String userEmail, @Param("userId") String userId);

    // 사용자 ID로 사용자 정보 조회
    Optional<User> findByUserKey(Integer userKey);

    // 회원 비활성화
    @Query("UPDATE User u SET u.userState = 0, u.deactiveuntil = CURRENT_DATE + INTERVAL '60' DAY WHERE u.userKey = :userKey")
    void deactivateUser(@Param("userKey") Integer userKey);

    // 탈퇴 회원 정보 저장
    @Query("INSERT INTO WithdrawUser (userKey) VALUES (:userKey)")
    void insertDeletedUser(@Param("userKey") Integer userKey);

    // 비밀번호 확인
    @Query("SELECT COUNT(u) FROM User u WHERE u.userKey = :userKey AND u.userPassword = :userPassword")
    Integer checkPassword(@Param("userKey") Integer userKey, @Param("userPassword") String userPassword);

    // 활성 사용자 확인
    @Query("SELECT COUNT(u) FROM User u WHERE u.userId = :userId AND u.userState = 1")
    Integer checkUserIsActive(@Param("userId") String userId);

    // 로그인 시 비활성화 기간 확인
    @Query("SELECT COUNT(u) FROM User u WHERE u.userId = :userId AND u.deactiveuntil > CURRENT_DATE")
    Integer checkDeactivationPeriod(@Param("userId") String userId);

    // 프로필 이미지 수정
    @Query("UPDATE User u SET u.profileImage = :profileImage WHERE u.userKey = :userKey")
    void updateProfileImage(@Param("profileImage") String profileImage, @Param("userKey") Integer userKey);
}
