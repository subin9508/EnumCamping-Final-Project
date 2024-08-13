package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer>, NoticeQuerydsl{
	
	List<Notice> findAllByOrderByIdDesc();

    @Modifying
    @Query("UPDATE Notice n SET n.title = :title, n.content = :content WHERE n.id = :id")
    void updateNotice(@Param("id") Long id, @Param("title") String title, @Param("content") String content);

}
