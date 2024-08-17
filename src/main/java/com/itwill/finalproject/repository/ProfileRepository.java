package com.itwill.finalproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.Profile;
import com.itwill.finalproject.domain.User;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
	
	// 사용자로 프로필을 찾는 메서드
	Optional<Profile> findByUser(User user);
}
