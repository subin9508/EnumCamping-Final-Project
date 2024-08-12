package com.itwill.finalproject.repository;

import java.util.List;

import com.itwill.finalproject.domain.Notice;
import com.itwill.finalproject.dto.NoticeSearchDto;


public interface NoticeQuerydsl {
	
	//제목/내용/제목+내용 검색
	List<Notice> searchByCategory(NoticeSearchDto dto);

}
