package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer>, NoticeQuerydsl{
	
	List<Notice> findAllByOrderByIdDesc();

	

}
