package com.itwill.finalproject.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  
    
    public QnAAnswers create(QnAAnswerRegisterDto dto) {
        log.info("create(dto={})", dto);
        log.info("QnA Content in DTO: {}", dto.getContents()); // 추가된 로
        
        // 댓글이 달릴 QnA 엔터티를 검색:
        QnA qna = qnaRepo.findById(dto.getQnaPostId()).orElseThrow();
        
        // DB 테이블에 저장할 QnA 타입의 엔터티를 생성:
        QnAAnswers entity = QnAAnswers.builder()
                .qna(qna)
                .contents(dto.getContents())
                .userId(dto.getUserId())
                .build();
        
        // DB에 저장(insert 쿼리 실행)
        qnaAnswerRepo.save(entity);
        
        return entity;
    }
    
    @Transactional(readOnly = true)
    public Page<QnAAnswers> readCommentsList(Long id, int pageNo) {
        log.info("readCommentsList(id={}, pageNo={})", id, pageNo);
        
        // 댓글들이 달려 있는 포스트 엔터티를 검색:
        QnA qna = qnaRepo.findById(id).orElseThrow();
        
        // 페이징 처리와 정렬을 하기 위한 Pageable 객체 생성:
        Pageable pageable = PageRequest.of(pageNo, 5, Sort.by("modifiedTime").descending());
        
        // DB에서 검색(select 쿼리를 실행)
        Page<QnAAnswers> data = qnaAnswerRepo.findByQna(qna, pageable);
        log.info("data.number = {}, data.totalPages = {}",
                data.getNumber(), data.getTotalPages());
        
        return data;
    }
    
    public void delete(Long id) {
        log.info("delete(id={})", id);
        
        qnaAnswerRepo.deleteById(id);
    }
    
    @Transactional
    //-> 엔터티를 findById 등의 메서드로 검색한 후, 엔터티가 변경되면 자동으로 update 쿼리가 실행됨.
    //-> JpaRepository<T, ID>.save(entity) 메서드를 명시적으로 호출할 필요가 없음. 
    public void update(QnAAnswerUpdateDto dto) {
        log.info("update(dto={})", dto);
        
        // 아이디(PK)로 엔터티를 검색:
        QnAAnswers entity = qnaAnswerRepo.findById(dto.getId()).orElseThrow();
        
        // 검색된 엔터티의 필드를 업데이트:
        entity.update(dto.getContents());
        
        // commentRepo.save(entity)를 명시적으로 호출할 필요 없음.
    }

    // QnA ID로 해당 QnA에 대한 모든 답변을 조회하는 메서드
    public List<QnAAnswers> findByQnaId(Long qnaId) {
        // Repository에서 QnA ID에 해당하는 답변 목록 조회
        return qnaAnswerRepo.findByQnaId(qnaId);
    }
    
}