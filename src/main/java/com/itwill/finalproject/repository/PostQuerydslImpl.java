package com.itwill.finalproject.repository;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.itwill.finalproject.domain.QnA;

public class PostQuerydslImpl extends QuerydslRepositorySupport implements PostQuerydsl {
	
	public PostQuerydslImpl() {
		super(QnA.class);
	}
	
}
