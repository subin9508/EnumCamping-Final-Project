package com.itwill.finalproject.service;

import org.springframework.stereotype.Service;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.dto.QnACreateDto;
import com.itwill.finalproject.repository.QnARepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class QnAService {
	
	private final QnARepository qnaRepo;
	
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
}
