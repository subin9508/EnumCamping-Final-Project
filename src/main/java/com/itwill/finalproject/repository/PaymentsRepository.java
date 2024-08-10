package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.dto.PaymentsDto;
import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
    
    // 예약 아이디로 결제 정보를 조회
    Optional<Payments> findByResId(Integer resId);
    
    // 새로운 결제 정보를 삽입 (JpaRepository의 save 메소드 사용)
    
    
    // 결제 정보(dto)를 기반으로 결제 아이디를 조회
    @Query("SELECT p.payId FROM Payments p WHERE p.impUid = :#{#dto.impUid}")
    Integer findPayIdByPaymentsDto(@Param("dto") PaymentsDto dto);
    
    // 결제 정보 업데이트 (JpaRepository의 save 메소드 사용)
    Payments save(PaymentsDto dto);
    // 예약 상태 업데이트
    @Modifying
    @Query("UPDATE ReservationMaster r SET r.resState = :resState WHERE r.resId = :resId")
    int updateReservationState(@Param("resId") Integer resId, @Param("resState") Integer resState);
    
    // imp_uid로 결제 정보 조회하여 결제에 연결된 예약 ID 반환 (웹훅시 사용)
    // @Query("SELECT p.resId FROM Payment p WHERE p.impUid = :impUid")
    // Integer findResIdByImpUid(@Param("impUid") String impUid);
}