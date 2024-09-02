package com.itwill.finalproject.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.Items;

public interface ItemsRepository extends JpaRepository<Items, Integer>{
	@Query("select i from Items i "
			+ "where i.itemId >= 21")
	List<Items> selectAllItems();
	
	@Query("select i from Items i "
			+ "where i.itemId <= 20")
	List<Items> selectAllZones();
	
	@Query("select i.itemPrice from Items i "
			+ "where i.itemId = :itemId and i.special = 1")
	Integer selectItemPrice(@Param("itemId") int itemId);
	


	
}
