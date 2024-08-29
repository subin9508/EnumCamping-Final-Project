package com.itwill.finalproject.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ItemsHistory;

public interface ItemsHistoryRepository extends JpaRepository<ItemsHistory, Integer>{
    ItemsHistory findTopByItemsOrderByStartDateDesc(Items items);
    
    List<ItemsHistory> findByItems_ItemId(Integer itemId);
    
    Optional<ItemsHistory> findTopByItems_ItemIdOrderByStartDateDesc(Integer itemId);
    
    // 가장 최근 start_date 조회
    @Query(value = "SELECT ih.start_date FROM itemshistory ih WHERE ih.item_id = :itemId AND ih.special = 1 ORDER BY ih.start_date DESC LIMIT 1", nativeQuery = true)
    LocalDateTime findLatestStartDateByItemIdAndSpecial(@Param("itemId") int itemId);

    // 특정 조건에 맞는 item_price 조회
    @Query(value = "SELECT ih.item_price FROM itemshistory ih WHERE ih.item_id = :itemId AND ih.special = 0 AND DATE_SUB(:date, INTERVAL 1 SECOND) = ih.end_date", nativeQuery = true)
    Integer findItemPriceByItemIdAndAdjustedEndDate(@Param("itemId") int itemId, @Param("date") LocalDateTime date);
}