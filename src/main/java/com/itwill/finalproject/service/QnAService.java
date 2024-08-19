package com.itwill.finalproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
	
    // 현재 인증된 사용자의 ID를 가져오는 메서드
    private String getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
	
    @Transactional(readOnly = true)
    public Page<QnAListItemDto> read(int pageNo, Sort sort) {
        log.info("read(pageNo={}, sort={})", pageNo, sort);
        
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(pageNo, 5, sort);
        
        // 영속성(persistence/repository) 계층의 메서드를 호출해서 엔터티들의 리스트를 가져옴.
        Page<QnA> list = qnaRepo.findAll(pageable);
        list.forEach(qna -> log.info("QnA ID: {}, Title: {}, UserId: {}, ViewCnt: {}", qna.getId(), qna.getTitle(), qna.getQnaUserId(), qna.getQnaViewCnt()));
        log.info("page.totalPages = {}", list.getTotalPages()); // 전체 페이지 개수
        log.info("page.number = {}", list.getNumber()); // 현재 페이지 번호
        log.info("page.hasPrevious = {}", list.hasPrevious()); // 이전 페이지가 있는 지 여부
        log.info("page.hasNext = {}", list.hasNext()); // 다음 페이지가 있는 지 여부
        
        Page<QnAListItemDto> qnas = list.map(QnAListItemDto::fromEntity);
        
        return qnas;
    }
	
    @Transactional
    public Long create(QnACreateDto dto) {
        String authenticatedUserId = getAuthenticatedUserId();
        log.info("create(dto={}, authenticatedUserId={})", dto, authenticatedUserId);
        
        if (authenticatedUserId == null) {
            throw new IllegalStateException("User must be authenticated to create a QnA post.");
        }
        
        dto.setQnaUserId(authenticatedUserId);
        
        QnA entity = dto.toEntity();
        log.info("Converted to entity: {}", entity);

        qnaRepo.save(entity);
        log.info("Saved QnA: {}", entity);
        
        return entity.getId();
    }
	
	
	@Transactional
	public QnA readById(Long id) {
		log.info("readById(id={}", id);
		
		QnA entity = qnaRepo.findById(id).orElseThrow();
		log.info("entity = {}", entity);
		
//		entity.incrementViewCount(); // 조회수 증가
        qnaRepo.save(entity); // 변경사항 저장
		
		return entity;
	}

    @Transactional
    public void delete(Long id) {
        String authenticatedUserId = getAuthenticatedUserId();
        log.info("delete(id={}, authenticatedUserId={})", id, authenticatedUserId);
        
        QnA entity = qnaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid QnA ID: " + id));

        // 작성자만 삭제할 수 있도록 체크
        if (!entity.getQnaUserId().equals(authenticatedUserId)) {
            throw new SecurityException("You are not authorized to delete this QnA post.");
        }

        qnaRepo.deleteById(id);
    }
    
    @Transactional
    public void update(QnAUpdateDto dto) {
        String authenticatedUserId = getAuthenticatedUserId();
        log.info("update(dto={}, authenticatedUserId={})", dto, authenticatedUserId);
        
        QnA entity = qnaRepo.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid QnA ID: " + dto.getId()));
        
        // 작성자만 수정할 수 있도록 체크
        if (!entity.getQnaUserId().equals(authenticatedUserId)) {
            throw new SecurityException("You are not authorized to update this QnA post.");
        }
        
     // 비밀글 상태를 업데이트
        entity.setQnaLock(dto.getQnaLock());
        
        entity.update(dto.getTitle(), dto.getContent());
        
        qnaRepo.save(entity); // 변경사항을 저장해야 modifiedTime이 갱신
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
            result = qnaRepo.findByQnaUserIdContainingIgnoreCase(dto.getKeyword(), pageable);
            break;
        }
        log.info("totalPages = {}", result.getTotalPages());
        
        return  result.map(QnAListItemDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
//    public Page<QnAListItemDto> readByUserId(String userId, int pageNo, Sort sort) {
//        String userId = getAuthenticatedUserId();
//        log.info("readByUserId(userId={}, pageNo={}, sort={})", userId, pageNo, sort);
//        
//        Pageable pageable = PageRequest.of(pageNo, 5, sort);
//        Page<QnA> list = qnaRepo.findByQnaUserId(userId, pageable);
//        
//        return list.map(QnAListItemDto::fromEntity);
//    }
    
    
    public Page<QnAListItemDto> readByUserId(String userId, int pageNo, Sort sort) {
    	Pageable pageable = PageRequest.of(pageNo, 5, sort);
    	Page<QnA> list = qnaRepo.findByQnaUserId(userId, pageable);
    	
    	return list.map(QnAListItemDto::fromEntity);
    
}
    
    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
    
//    @Transactional
//    public void incrementViewCount(Long id) {
//        QnA qna = qnaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid QnA ID: " + id));
//        qna.incrementViewCount(); // 조회수 증가
//        qnaRepo.save(qna); // 변경사항 저장
//    }
    
    @Transactional
    public QnA incrementViewCount(Long qnaId, String userId, Integer userRole) {
        QnA qna = qnaRepo.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid QnA ID: " + qnaId));

        // 비밀글이 아니거나, 비밀글이고 작성자 또는 관리자인 경우에만 조회수 증가
        if (!qna.isSecret() || (userId != null && (userId.equals(qna.getQnaUserId()) || userRole == 0))) {
            qna.setQnaViewCnt(qna.getQnaViewCnt() + 1);
            qnaRepo.save(qna);
        }

        return qna;
    }
}
