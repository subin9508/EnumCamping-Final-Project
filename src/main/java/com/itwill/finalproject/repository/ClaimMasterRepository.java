package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.ClaimMaster;
import com.itwill.finalproject.domain.ClaimMasterId;

public interface ClaimMasterRepository extends JpaRepository<ClaimMaster, Integer>{

}
