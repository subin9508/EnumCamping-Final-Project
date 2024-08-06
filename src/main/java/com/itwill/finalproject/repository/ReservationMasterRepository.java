package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.ReservationMaster;

public interface ReservationMasterRepository extends JpaRepository<ReservationMaster, Integer> {

}
