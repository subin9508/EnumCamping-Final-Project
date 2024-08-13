package com.itwill.finalproject.repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.itwill.finalproject.domain.QQnA;
import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.dto.QnASearchRequestDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QnAQuerydslImpl extends QuerydslRepositorySupport implements QnAQuerydsl {

    public QnAQuerydslImpl() {
        super(QnA.class);
    }

    @Override
    public QnA searchById(Long id) {
        log.info("searchById(id={})", id);
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna); // select p from Post p
        query.where(qna.id.eq(id)); // query + where id = ?
        QnA entity = query.fetchOne();
        
        return entity;
    }
    
    @Override
    public List<QnA> searchByTitle(String keyword) {
        log.info("searchByTitle(keyword={})", keyword);
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna); // select
        query.where(qna.title.containsIgnoreCase(keyword)); // where
        query.orderBy(qna.id.desc()); // order by
        
        List<QnA> result = query.fetch();
        
        return result;
    }
    
    @Override
    public List<QnA> searchByContent(String keyword) {
        log.info("searchByContent(keyword={})", keyword);
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna)
                .where(qna.content.containsIgnoreCase(keyword))
                .orderBy(qna.id.desc());
        
        return query.fetch();
    }
    
    @Override
    public List<QnA> searchByTitleOrContent(String keyword) {
        log.info("searchByTitleOrContent(keyword={})", keyword);
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna);
        query.where(
        		qna.title.containsIgnoreCase(keyword)
                .or(qna.content.containsIgnoreCase(keyword))
        );
        query.orderBy(qna.id.desc());
        
        return query.fetch();
    }
    
    @Override
    public List<QnA> searchByModifiedTime(LocalDateTime from, LocalDateTime to) {
        log.info("searchByModifiedTime(from={}, to={})", from, to);
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna)
                .where(qna.modifiedTime.between(from, to))
                .orderBy(qna.modifiedTime.desc());
        
        return query.fetch();
    }
    
    @Override
    public List<QnA> searchByQnaUserIdAndTitle(String qnaUserId, String title) {
        log.info("searchByAuthorAndTitle(qnaUserId={}, title={})", qnaUserId, title);
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna)
                .where(qna.qnaUserId.eq(qnaUserId)
                        .and(qna.title.containsIgnoreCase(title)))
                .orderBy(qna.id.desc());
        
        return query.fetch();
    }
    
    @Override
    public List<QnA> searchByCategory(QnASearchRequestDto dto) {
        log.info("searchByCategory(dto={})", dto);
        String category = dto.getCategory();
        String keyword = dto.getKeyword();
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna);
        
        // BooleanBuilder: where() 메서드의 아규먼트인 BooleanExpression 객체를 생성할 수 있는 객체
        BooleanBuilder builder = new BooleanBuilder();
        switch (category) {
        case "t":
            builder.and(qna.title.containsIgnoreCase(keyword));
            break;
        case "c":
            builder.and(qna.content.containsIgnoreCase(keyword));
            break;
        case "tc":
            builder.and(qna.title.containsIgnoreCase(keyword))
                .or(qna.content.containsIgnoreCase(keyword));
            break;
        case "a":
            builder.and(qna.qnaUserId.containsIgnoreCase(keyword));
            break;
        }
        query.where(builder).orderBy(qna.id.desc());
        
        return query.fetch();
    }
    
    @Override
    public List<QnA> searchByKeywords(String[] keywords) {
        log.info("searchByKeywords(keywords={})", Arrays.asList(keywords));
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna);
        BooleanBuilder builder = new BooleanBuilder();
        for (String k : keywords) {
            builder.or(qna.title.containsIgnoreCase(k)
                    .or(qna.content.containsIgnoreCase(k)));
        }
        query.where(builder).orderBy(qna.id.desc());
        
        return query.fetch();
    }
    
    @Override
    public Page<QnA> searchByKeywords(String[] keywords, Pageable pageable) {
        log.info("searchByKeywords(keyword={}, Pageable={})",
                Arrays.asList(keywords), pageable);
        
        QQnA qna = QQnA.qna;
        JPQLQuery<QnA> query = from(qna);
        BooleanBuilder builder = new BooleanBuilder();
        for (String k : keywords) {
            builder.or(qna.title.containsIgnoreCase(k)
                    .or(qna.content.containsIgnoreCase(k)));
        }
        query.where(builder);
        
        // Paging & Sorting 적용
        getQuerydsl().applyPagination(pageable, query);
        
        // 한 페이지에 표시할 데이터를 fetch.
        List<QnA> list = query.fetch();
        log.info("list.size = {}", list.size());
        
        // 전체 레코드 개수를 fetch.
        long count = query.fetchCount();
        log.info("fetch count = {}", count);
        
        // Page<T> 객체를 생성.
        Page<QnA> page = new PageImpl<>(list, pageable, count);
        
        return page;
    }
    
}