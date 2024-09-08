package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.ClaimDetail;
import com.itwill.finalproject.dto.ClaimDetailDto;

public interface ClaimDetailRepository extends JpaRepository<ClaimDetail, Integer> {
	
	List<ClaimDetail> findByClmId(Integer clmId);

}