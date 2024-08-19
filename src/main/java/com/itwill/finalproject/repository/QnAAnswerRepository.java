package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.domain.QnAAnswers;

public interface QnAAnswerRepository extends JpaRepository<QnAAnswers, Long> {

//		Page<QnAAnswers> findByQna(QnA qna, Pageable pageable);
		
		List<QnAAnswers> findByQnaId(Long id);
		
		List<QnAAnswers> findByQna(QnA qna);

}