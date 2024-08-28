package com.itwill.finalproject.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ItemsHistory;

public interface ItemsHistoryRepository extends JpaRepository<ItemsHistory, Integer>{
    ItemsHistory findTopByItemsOrderByStartDateDesc(Items items);
    
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO itemshistory (item_id, item_price, start_date) VALUES (:itemId, :itemPrice, :startDate)", nativeQuery = true)
    void insertIntoHistory(int itemId, int itemPrice, LocalDateTime startDate );

}