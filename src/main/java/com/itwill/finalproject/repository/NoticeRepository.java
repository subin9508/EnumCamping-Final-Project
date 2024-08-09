package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer>, NoticeQuerydsl{
	

}
