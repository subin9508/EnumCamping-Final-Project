package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.ClaimMaster;

public interface ClaimMasterRepository extends JpaRepository<ClaimMaster, Integer>{
	
	@Query("SELECT clm "
			+ "FROM ClaimMaster clm "
			+ "WHERE clmId = (SELECT MAX(clmId) FROM ClaimMaster WHERE resId = :resId)")
	ClaimMaster findByResIdMaxClmId(@Param("resId") Integer resId);
	
}
