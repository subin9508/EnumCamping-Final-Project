package com.itwill.finalproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.domain.QnAAnswer;

public interface QnAAnswerRepository extends JpaRepository<QnAAnswer, Long> {

		Page<QnAAnswer> findByQna(QnA qna, Pageable pageable);
}
