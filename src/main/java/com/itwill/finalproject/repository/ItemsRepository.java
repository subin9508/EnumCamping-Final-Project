package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.Items;

public interface ItemsRepository extends JpaRepository<Items, Integer>{

}
