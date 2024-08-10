package com.itwill.finalproject.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itwill.finalproject.domain.QnA;

public interface PostQuerydsl {
//    // id가 일치하는 엔터티 검색
//    QnA searchById(Long id);
//    
//    // title에 포함된 문자열 대소문자 구분없이 검색
//    List<QnA> searchByTitle(String keyword);
//    
//    // content에 포함된 문자열 대소문자 구분없이 검색
//    List<QnA> searchByContent(String keyword);
//    
//    // 제목 또는 내용에 포함된 문자열 대소문자 구분없이 검색
//    List<QnA> searchByTitleOrContent(String keyword);
//    
//    // 수정시간 범위로 검색: where modified_time between ? and ?
//    List<QnA> searchByModifiedTime(LocalDateTime from, LocalDateTime to);
//    
//    // 작성자와 제목으로 검색: where author = ? and lower(title) like ?
//    List<QnA> searchByAuthorAndTitle(String author, String title);
//    
//    // 제목/내용/제목+내용/작성자 검색
//    List<QnA> searchByCategory(PostSearchRequestDto dto);
//    
//    // 제목 또는 내용에 검색어들 중 한 개라도 포함되어 있는 레코드들을 검색
//    List<QnA> searchByKeywords(String[] keywords);
//    
//    // Paging 처리
//    Page<QnA> searchByKeywords(String[] keywords, Pageable pageable);
}