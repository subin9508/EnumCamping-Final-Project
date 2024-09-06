package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.dto.PaymentsDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
    
    // 예약 아이디로 결제 정보를 조회
    Optional<Payments> findByResId(Integer resId);
    
    // 예약 아이디 중 제일 높은 payId찾기
    @Query("SELECT p FROM Payments p WHERE p.resId = :resId ORDER BY p.payId DESC")
    Optional<Payments> findTopByResIdOrderByPayIdDesc(@Param("resId") Integer resId);

    
    // 새로운 결제 정보를 삽입 (JpaRepository의 save 메소드 사용)
    
    
    // 결제 정보(dto)를 기반으로 결제 아이디를 조회
    @Query("SELECT p.payId FROM Payments p WHERE p.impUid = :#{#dto.impUid}")
    Integer findPayIdByPaymentsDto(@Param("dto") Payments payments);
    
    // 결제 정보 업데이트 (JpaRepository의 save 메소드 사용)
    Payments save(Payments payments);
    
    // 예약 상태 업데이트
    @Modifying
    @Query("UPDATE ReservationMaster r SET r.resState = :resState WHERE r.resId = :resId")
    int updateReservationState(@Param("resId") Integer resId, @Param("resState") Integer resState);
    
    // 결제 ID를 통해 impUid 조회하는 메서드
    @Query("SELECT p.impUid FROM Payments p WHERE p.payId = :payId")
    String getImpUidByPayId(@Param("payId") Integer payId);
    
    // 결제 ID를 통해 payStatus 조회하는 메서드
    @Query("SELECT p.payStatus FROM Payments p WHERE p.payId = :payId")
    String getPayStatus(@Param("payId") Integer payId);
    
    // 예약 ID로 결제 내역을 조회하고 가장 최신의 결제를 반환
    @Query("SELECT p FROM Payments p WHERE p.resId = :resId ORDER BY p.payDate DESC")
    Optional<Payments> findLatestPaymentByResId(@Param("resId") Integer resId);
    
    // 특정 예약(resId)에 대한 결제 내역을 결제 날짜(PayDate) 기준으로 내림차순으로 정렬하여 가져오는 메서드
    List<Payments> findByResIdOrderByPayDateDesc(Integer resId);
    

    // resID와 payState paid 조건 기반으로 가장 최신의 payID 조회
//    @Query("SELECT p FROM Payments p WHERE p.resId = :resId AND p.payStatus = 'paid' ORDER BY p.payId DESC")
//    Optional<Payments> findMostRecentPaidPaymentByResId(@Param("resId") Integer resId);
    
    // 가장 최신의 결제 정보를 조회하는 메서드
    Optional<Payments> findTopByResIdAndPayStatusOrderByPayIdDesc(Integer resId, String payStatus);

    
    // imp_uid로 결제 정보 조회하여 결제에 연결된 예약 ID 반환 (웹훅시 사용)
    // @Query("SELECT p.resId FROM Payment p WHERE p.impUid = :impUid")
    // Integer findResIdByImpUid(@Param("impUid") String impUid);
}