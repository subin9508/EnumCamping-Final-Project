package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.ClaimDateDetail;

public interface ClaimDateDetailRepository extends JpaRepository<ClaimDateDetail, Integer>{
	
	ClaimDateDetail findByClmId(Integer ClmId);
}
