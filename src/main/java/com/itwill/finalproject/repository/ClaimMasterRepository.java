package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.ClaimMaster;

public interface ClaimMasterRepository extends JpaRepository<ClaimMaster, Integer>{
	
	@Query("SELECT clm "
			+ "FROM ClaimMaster clm "
			+ "WHERE clmId = (SELECT MAX(clmId) FROM ClaimMaster WHERE resId = :resId)")
	ClaimMaster findByResIdMaxClmId(@Param("resId") Integer resId);
	
	List<ClaimMaster> findByResId(Integer resId);
	
	@Query("SELECT clm "
			+ "FROM ClaimMaster clm "
			+ "WHERE clm.resId = ( "
			+ "    SELECT p.resId "
			+ "    FROM Payments p "
			+ "    WHERE p.payId = :payId "
			+ ") "
			+ "ORDER BY clm.clmId DESC "
			+ "LIMIT 1 ")
	ClaimMaster findByPayIdMaxClmId(@Param("payId") Integer payId);
}