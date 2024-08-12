package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.dto.ReservationDetailListDto;

@Repository
public interface ReservationDetailRepository extends JpaRepository<ReservationDetail, Integer> {

	 // ResId에 해당하는 물품 리스트 조회
    List<ReservationDetailListDto> findByRdId(Integer rdId);
    
//    // userId에 해당하는 예약 상세 정보 조회
//    List<ReservationListDto> findByUserId(String userId);
	
	
}
