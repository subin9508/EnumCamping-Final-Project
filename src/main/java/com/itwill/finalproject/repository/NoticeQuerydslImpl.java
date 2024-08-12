package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.itwill.finalproject.domain.Notice;
import com.itwill.finalproject.domain.QNotice;
import com.itwill.finalproject.dto.NoticeSearchDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoticeQuerydslImpl extends QuerydslRepositorySupport implements NoticeQuerydsl{

	public NoticeQuerydslImpl() {
		super(Notice.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Notice> searchByCategory(NoticeSearchDto dto) {
		log.info("searchByCategory(dto={})",dto);
		String category = dto.getCategory();
		String keyword = dto.getKeyword();
		
		QNotice notice = QNotice.notice;
		JPQLQuery<Notice> query = from(notice);
		
		// where() 메서드의 아규먼트인 BooleanExpression 객체를 생성할 수 있는 객체
		BooleanBuilder builder = new BooleanBuilder();
		switch(category) {
		case "t":
			builder.and(notice.title.containsIgnoreCase(keyword));
			break;
		case "c":
			builder.and(notice.content.containsIgnoreCase(keyword));
			break;	
		case "tc":
			builder.and(notice.title.containsIgnoreCase(keyword))
			.or(notice.content.containsIgnoreCase(keyword));
			break;	
		}
		
		query.where(builder).orderBy(notice.id.desc());
		return query.fetch();
	}
	
	
	
	

}
