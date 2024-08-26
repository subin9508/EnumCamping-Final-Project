package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ItemsHistory;

public interface ItemsHistoryRepository extends JpaRepository<ItemsHistory, Integer>{
    ItemsHistory findTopByItemsOrderByStartDateDesc(Items items);
}