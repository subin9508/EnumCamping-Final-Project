package com.itwill.finalproject.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.domain.QnAAnswers;
import com.itwill.finalproject.dto.QnAAnswerRegisterDto;
import com.itwill.finalproject.dto.QnAAnswerUpdateDto;
import com.itwill.finalproject.repository.QnAAnswerRepository;
import com.itwill.finalproject.repository.QnARepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class QnAAnswerService {
    
    private final QnAAnswerRepository qnaAnswerRepo;
    private final QnARepository qnaRepo;

    // 새로운 답변 생성
    @Transactional
    public QnAAnswers create(QnAAnswerRegisterDto dto) {
        log.info("create(dto={})", dto);
        log.info("QnA Content in DTO: {}", dto.getContents());
        
        // 현재 사용자 ID를 설정
        dto.setUserId(getCurrentUserId());

        QnA qna = qnaRepo.findById(dto.getQnaPostId()).orElseThrow();

        QnAAnswers entity = QnAAnswers.builder()
                .qna(qna)
                .contents(dto.getContents())
                .userId(dto.getUserId())
                .build();
        
        log.info("ENTITY: {}", entity);
        
        qnaAnswerRepo.save(entity);
        log.info("SAVE ENTITY: {}", entity);
        
        return entity;
    }
    
    @Transactional(readOnly = true)
    public Page<QnAAnswers> readCommentsList(Long id, int pageNo) {
        log.info("readCommentsList(id={}, pageNo={})", id, pageNo);
        
        QnA qna = qnaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid QnA ID: " + id));
        
        Pageable pageable = PageRequest.of(pageNo, 5, Sort.by("modifiedTime").descending());
        
        Page<QnAAnswers> data = qnaAnswerRepo.findByQna(qna, pageable);
        log.info("data.number = {}, data.totalPages = {}",
                data.getNumber(), data.getTotalPages());
        
        if(data.isEmpty()) {
            log.warn("No QnAAnswers found for QnA ID: {}", id);
        }
        
        return data;
    }
    
    @Transactional
    public void delete(Long id) {
        log.info("delete(id={})", id);

        QnAAnswers answer = qnaAnswerRepo.findById(id).orElseThrow();
        checkIfUserIsAuthorized(answer.getUserId());

        qnaAnswerRepo.deleteById(id);
    }
    
    @Transactional
    public void update(QnAAnswerUpdateDto dto) {
        log.info("update(dto={})", dto);
        
        QnAAnswers entity = qnaAnswerRepo.findById(dto.getId()).orElseThrow();
        checkIfUserIsAuthorized(entity.getUserId());

        entity.update(dto.getContents());
    }

    public List<QnAAnswers> findByQnaId(Long qnaId) {
        return qnaAnswerRepo.findByQnaId(qnaId);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); 
    }

    private void checkIfUserIsAuthorized(String userId) {
        String currentUserId = getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new SecurityException("You are not authorized to modify this answer.");
        }
    }
}
