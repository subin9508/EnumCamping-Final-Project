package com.itwill.finalproject.repository;

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
    
//    @Modifying
//    @Query("UPDATE Special s SET s.endDate = :endDate WHERE s.items.id = :itemId")
//    int updateEndDateByItemId(@Param("itemId") Integer itemId, 
//                               @Param("endDate") LocalDateTime endDate);
    
//    @Modifying
//    @Query("update Special s set s.endDate = :endDate where s.items.itemId = :itemId and s.endDate > CURRENT_TIMESTAMP")
//    int updateEndDateByItemId(@Param("itemId") Integer itemId, @Param("endDate") LocalDateTime endDate);
    
//    @Modifying
//    @Transactional
//    @Query("UPDATE Special s SET s.endDate = (SELECT max(h.endDate) "
//    		+ "FROM ItemsHistory h WHERE h.items.itemId = :itemId AND h.special = 1) "
//    		+ "WHERE s.items.itemId = :itemId")
//    int updateSpecialEndDateFromHistory(@Param("itemId") Integer itemId);

    
//    @Modifying
//    @Transactional
//    @Query("UPDATE Special s SET s.endDate = CURRENT_TIMESTAMP "
//    		+ "WHERE s.items.itemId = :itemId AND "
//    		+ "EXISTS (SELECT 1 FROM ItemsHistory h "
//    		+ "WHERE h.items.itemId = s.items.itemId AND h.special = 0 "
//    		+ "AND h.endDate >= CURRENT_TIMESTAMP)")
//    int updateSpecialEndDateWhenSpecialGoesZero(@Param("itemId") Integer itemId);
    
//    @Modifying
//    @Transactional
//    @Query(value = "UPDATE special s SET s.end_date = CURRENT_TIMESTAMP "
//                 + "WHERE s.item_id = :itemId AND "
//                 + "EXISTS (SELECT 1 FROM itemshistory h "
//                 + "WHERE h.item_id = s.item_id AND h.special = 0 "
//                 + "AND h.end_date >= CURRENT_TIMESTAMP LIMIT 1)", nativeQuery = true)
//    int updateSpecialEndDateWhenSpecialGoesZero(@Param("itemId") Integer itemId);
    
    
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE special s SET s.end_date = CURRENT_TIMESTAMP "
                 + "WHERE s.item_id = :itemId "
                 + "order by s.start_date desc LIMIT 1", nativeQuery = true)
    int updateSpecialEndDateWhenSpecialGoesZero(@Param("itemId") Integer itemId);

    
    
    // 특정 itemId에 대한 특가 종료일을 조회하는 메서드	
    @Query("SELECT s.endDate FROM Special s WHERE s.items.itemId = :itemId")
    Optional<LocalDateTime> findEndDateByItemId(@Param("itemId") Integer itemId);
}
