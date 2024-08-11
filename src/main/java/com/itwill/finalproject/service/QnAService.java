package com.itwill.finalproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.dto.QnACreateDto;
import com.itwill.finalproject.dto.QnAListItemDto;
import com.itwill.finalproject.dto.QnASearchRequestDto;
import com.itwill.finalproject.dto.QnAUpdateDto;
import com.itwill.finalproject.repository.QnARepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class QnAService {
	
	private final QnARepository qnaRepo;
	
	
    @Transactional(readOnly = true)
    public Page<QnAListItemDto> read(int pageNo, Sort sort) {
        log.info("read(pageNo={}, sort={})", pageNo, sort);
        
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(pageNo, 5, sort);
        
        // 영속성(persistence/repository) 계층의 메서드를 호출해서 엔터티들의 리스트를 가져옴.
        Page<QnA> list = qnaRepo.findAll(pageable);
        log.info("page.totalPages = {}", list.getTotalPages()); // 전체 페이지 개수
        log.info("page.number = {}", list.getNumber()); // 현재 페이지 번호
        log.info("page.hasPrevious = {}", list.hasPrevious()); // 이전 페이지가 있는 지 여부
        log.info("page.hasNext = {}", list.hasNext()); // 다음 페이지가 있는 지 여부
        
        Page<QnAListItemDto> qnas = list.map(QnAListItemDto::fromEntity);
        
        return qnas;
    }
	
	@Transactional
	public Long create(QnACreateDto dto) {
		log.info("create(dto={})", dto);
		
		QnA entity = qnaRepo.save(dto.toEntity());
		log.info("entity = {}", entity);
		
		return entity.getId();
	}
	
	@Transactional
	public QnA readById(Long id) {
		log.info("readById(id={}", id);
		
		QnA entity = qnaRepo.findById(id).orElseThrow();
		log.info("entity = {}", entity);
		
		return entity;
	}

	@Transactional
    public void delete(Long id) {
        log.info("delete(id={})", id);
        
        qnaRepo.deleteById(id);
    }
    
    @Transactional
    public void update(QnAUpdateDto dto) {
        log.info("update(dto={})", dto);
        
        // id로 Post 엔터티 객체를 찾음(DB select 쿼리)
        QnA entity = qnaRepo.findById(dto.getId()).orElseThrow();
        
        // DB에서 검색한 엔터티 객체의 필드들을 업데이트(수정)
        entity.update(dto.getTitle(), dto.getContent());
        
        // @Transactional 애너테이션을 사용한 경우, 
        // DB에서 검색한 entity 객체가 변경되면 update 쿼리가 자동으로 실행.
        // @Transactional 애너테이션을 사용하지 않은 경우,
        // postRepo.save(entity) 메서드를 직접 호출해야 함.
    }
    
    @Transactional(readOnly = true)
    public Page<QnAListItemDto> search(QnASearchRequestDto dto, Sort sort) {
        log.info("search(dto={}, sort={})", dto, sort);
        
        Pageable pageable = PageRequest.of(dto.getP(), 5, sort);
        Page<QnA> result = null;
        switch (dto.getCategory()) {
        case "t":
            result = qnaRepo.findByTitleContainingIgnoreCase(dto.getKeyword(), pageable);
            break;
        case "c":
            result = qnaRepo.findByContentContainingIgnoreCase(dto.getKeyword(), pageable);
            break;
        case "tc":
            result = qnaRepo.findByTitleOrContent(dto.getKeyword(), pageable);
            break;
        case "a":
            result = qnaRepo.findByAuthorContainingIgnoreCase(dto.getKeyword(), pageable);
            break;
        }
        log.info("totalPages = {}", result.getTotalPages());
        
        return  result.map(QnAListItemDto::fromEntity);
    }
}
