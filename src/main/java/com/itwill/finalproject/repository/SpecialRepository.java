package com.itwill.finalproject.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.Special;

public interface SpecialRepository extends JpaRepository<Special, Integer> {
	
//    // 이 아이템이 특가인지 확인하는 JPQL 쿼리
//    @Query("SELECT s.items.id FROM Special s WHERE s.startDate <= :now AND s.endDate >= :now AND s.items.id = :itemId")
//    String isSpecial(@Param("itemId") int itemId, @Param("now") LocalDateTime now);
    
    // startdate 찾기 (없으면 특가 아닌 것)
    @Query("SELECT s.startDate FROM Special s WHERE s.startDate <= :now AND s.endDate >= :now AND s.items.id = :itemId")
    LocalDateTime findStartDate(@Param("itemId") int itemId, @Param("now") LocalDateTime now);
       
    
//    @Modifying
//    @Transactional
//    @Query("UPDATE Special s SET s.endDate = :endDate WHERE s.items.id = :itemId AND s.endDate = :defaultEndDate")
//    int updateEndDateByItemId(@Param("itemId") Integer itemId, 
//                               @Param("endDate") LocalDateTime endDate, 
//                               @Param("defaultEndDate") LocalDateTime defaultEndDate);
    
    @Modifying
    @Transactional
    @Query("UPDATE Special s SET s.endDate = :endDate WHERE s.items.id = :itemId")
    int updateEndDateByItemId(@Param("itemId") Integer itemId, 
                               @Param("endDate") LocalDateTime endDate);
    
    
    
    // 특정 productId에 대한 특가 종료일을 조회하는 메서드	
    @Query("SELECT s.endDate FROM Special s WHERE s.items.itemId = :itemId")
    Optional<LocalDateTime> findEndDateByItemId(@Param("itemId") Integer itemId);
}
