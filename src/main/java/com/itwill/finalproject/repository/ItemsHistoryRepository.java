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
      
 
    // 특가 항목을 가져오는 메서드 추가
    List<ItemsHistory> findBySpecial(int special);
	
	// 특정 Items에 대해 시작 날짜가 가장 최신인 항목을 조회
    ItemsHistory findTopByItemsOrderByStartDateDesc(Items items);
    
    // 특정 itemId에 대한 모든 ItemsHistory 항목을 조회
    List<ItemsHistory> findByItems_ItemId(Integer itemId);
    
    // 특정 itemId에 대해 시작 날짜가 가장 최신인 항목을 Optional로 조회
    Optional<ItemsHistory> findTopByItems_ItemIdOrderByStartDateDesc(Integer itemId);
    
    // 가장 최근 start_date 조회
    @Query(value = "SELECT ih.start_date FROM itemshistory ih WHERE ih.item_id = :itemId AND ih.special = 1 ORDER BY ih.start_date DESC LIMIT 1", nativeQuery = true)
    LocalDateTime findLatestStartDateByItemIdAndSpecial(@Param("itemId") int itemId);

    // 특정 조건에 맞는 item_price 조회
    @Query(value = "SELECT ih.item_price FROM itemshistory ih WHERE ih.item_id = :itemId AND ih.special = 0 AND :date = ih.end_date", nativeQuery = true)
    Integer findItemPriceByItemIdAndAdjustedEndDate(@Param("itemId") int itemId, @Param("date") LocalDateTime date);
    
    // 특정 itemId에 대한 special 1 항목의 가격을 start_date 기준 내림차순 정렬해서 가장 최근 항목 조회
    @Query(value = "SELECT ih.item_price FROM itemshistory ih WHERE ih.item_id = :itemId AND ih.special = 1 ORDER BY ih.start_date DESC LIMIT 1", nativeQuery = true)
    Integer findSpecialPrice(@Param("itemId") int itemId);
    
    @Query(value = "SELECT ih.item_price FROM itemshistory ih WHERE ih.item_id = :itemId AND ih.special = 0 ORDER BY ih.start_date DESC LIMIT 1", nativeQuery = true)
    Integer findNewestNormalPrice(@Param("itemId") int itemId);
    
    // 각 항목(item)에 대해 special 값이 0이고 start_date가 가장 최신인 항목의 가격을 조회
    @Query(value = "SELECT ih.item_price FROM itemshistory ih WHERE ih.special = 0 AND ih.start_date = (SELECT MAX(ih2.start_date) FROM itemshistory ih2 "
    		+ "WHERE ih2.item_id = ih.item_id AND ih2.special = 0) ORDER BY ih.item_id", nativeQuery = true)
    List<Integer> findLatestPricesWithSpecialZero();
    
    // 각 항목(item)에 대해 special 값이 1이고 start_date가 가장 최신인 항목의 가격을 조회
    @Query(value = "SELECT ih.item_price FROM itemshistory ih "
            + "JOIN (SELECT item_id, MAX(start_date) AS max_date FROM itemshistory WHERE special = 1 GROUP BY item_id) sub "
            + "ON ih.item_id = sub.item_id AND ih.start_date = sub.max_date "
            + "WHERE ih.special = 1 ORDER BY ih.item_id", nativeQuery = true)
    List<Integer> findLatestSpecialPrices();
    

}