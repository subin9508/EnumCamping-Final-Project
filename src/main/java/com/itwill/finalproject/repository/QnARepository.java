package com.itwill.finalproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.QnA;

public interface QnARepository extends JpaRepository<QnA, Long>, QnAQuerydsl {
    // JPA Query Method
    // 제목에 포함된 문자열 대소문자 구분없이 검색하기:
    Page<QnA> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    
    // 내용에 포함된 문자열 대소문자 구분없이 검색하기:
    Page<QnA> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
    
    // 작성자에 포함된 문자열 대소문자 구분없이 검색하기:
    Page<QnA> findByQnaUserIdContainingIgnoreCase(String keyword, Pageable pageable);
    
    // userId에 해당하는 qna
    Page<QnA> findByQnaUserId(String keyword, Pageable pageable);
    
    // JPQL(Java Persistence Query Language): 객체지향 쿼리 언어.
    // 제목 또는 내용에 포함된 문자열 대소문자 구분없이 검색하기:
    // findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(args)
    // findByTitleContainingOrContentContainingAllIgnoreCase(args)
    @Query("select p from QnA p "
            + "where upper(p.title) like upper('%' || :keyword || '%') "
            + "or upper(p.content) like upper('%' || :keyword || '%') ")
    Page<QnA> findByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);
    
}
