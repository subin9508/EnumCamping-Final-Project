package com.itwill.finalproject.service;

import org.springframework.stereotype.Service;

import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.UserUpdateDto;
import com.itwill.finalproject.repository.ReservationMasterRepository;
import com.itwill.finalproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {
	
	private final UserRepository userRepo;
	private final ReservationMasterRepository resRepo;
	
	
	// 사용자 정보 조회
	public User read(String userId) {
		log.debug("read(id={})", userId);
		
		// 사용자 ID를 이용해 사용자 정보를 조회
		return userRepo.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
		
	}
	
	
	
	// 사용자 정보 업데이트
	public void update(UserUpdateDto dto) {
		log.debug("update({})", dto);
		
		// DTO를 Entity로 변환하여 업데이트 수행
		User result = userRepo.save(dto.toEntity());
		log.debug("update 결과 = {}", result);
	
	}

}
