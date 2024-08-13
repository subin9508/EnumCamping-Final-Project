package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.Items;

public interface ItemsRepository extends JpaRepository<Items, Integer>{
	@Query("select i from Items i "
			+ "where i.itemId >= 21")
	List<Items> selectAllItems();
	
	@Query("select i.itemPrice from Items i "
			+ "where i.itemId = :itemId")
	Integer selectItemPrice(@Param("itemId") int itemId);
}
