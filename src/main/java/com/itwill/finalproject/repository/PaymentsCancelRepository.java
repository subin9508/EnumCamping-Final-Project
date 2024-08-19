package com.itwill.finalproject.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.domain.PaymentsCancel;
import com.itwill.finalproject.dto.PaymentsCancelDto;

@Repository
public interface PaymentsCancelRepository extends JpaRepository<PaymentsCancel, Integer> {
	// JpaRepository에서 기본적으로 제공하는 save 메서드를 사용하여 취소 기록을 저장
	
    // 취소 테이블 삽입
//    Integer insertPaymentCancel(PaymentsCancelDto cancelDto);
    
    @Modifying
    @Query("INSERT INTO PaymentsCancel (payId, impUid, resId, canAmount, canDate) VALUES (:payId, :impUid, :resId, :canAmount, :canDate)")
    Integer insertPaymentCancel(@Param("payId") Integer payId, 
                                @Param("impUid") String impUid, 
                                @Param("resId") Integer resId, 
                                @Param("canAmount") Integer canAmount, 
                                @Param("canDate") LocalDateTime canDate);
	
}