package com.itwill.finalproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.dto.PaymentsDto;


public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
	
	// 예약 아이디로 결제 정보를 조회
	Optional<Payments> findByResId(Integer resId);
	
	// 새로운 결제 정보를 삽입 (JpaRepository에서 자동으로 제공)
    // Payments save(Payments payment);
	
	// 결제 정보(dto)를 기반으로 결제 아이디를 조회
	@Query("SELECT p.payId FROM Payments p WHERE p.impUid = :#{#dto.impUid} AND p.pgTid = :#{#dto.pgTid}")
	Integer findPayIdByDto(@Param("dto") PaymentsDto dto);
	
	// 결제 정보 업데이트
	// Payments save(Payments payment);
	
//	// 예약 상태 업데이트
//	@Modifying
//	@Query("UPDATE Payments p SET p.resState = :resStatus WHERE p.resId = :resId")
//	int updateReservationStatus(@Param("resId") Integer resId, @Param("resStatus") Integer resStatus);
	
	// imp_uid로 결제 정보를 조회하여 결제에 연결된 예약 ID 반환
	@Query("SELECT p.resId FROM Payments p WHERE p.impUid = :impUid")
	Integer findResIdByImpUid(@Param("impUid") String impUid);
	
}
