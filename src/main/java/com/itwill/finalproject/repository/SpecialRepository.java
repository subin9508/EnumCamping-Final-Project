package com.itwill.finalproject.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.Special;

public interface SpecialRepository extends JpaRepository<Special, Integer> {
	
    // 이 아이템이 특가인지 확인하는 JPQL 쿼리
    @Query("SELECT s.items.id FROM Special s WHERE s.startDate <= :now AND s.endDate >= :now AND s.items.id = :itemId")
    String isSpecial(@Param("itemId") int itemId, @Param("now") LocalDateTime now);

}
