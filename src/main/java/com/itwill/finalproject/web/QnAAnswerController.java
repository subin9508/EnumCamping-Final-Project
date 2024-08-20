package com.itwill.finalproject.web;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.domain.QnAAnswers;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.QnAAnswerRegisterDto;
import com.itwill.finalproject.dto.QnAAnswerUpdateDto;
import com.itwill.finalproject.service.QnAAnswerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/qnaAnswers")
public class QnAAnswerController {

    private final QnAAnswerService qnaAnswerSvc;
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<QnAAnswers> registerComments(@RequestBody QnAAnswerRegisterDto dto) {
        log.info("registerComments(dto={})", dto);
        
        QnAAnswers entity = qnaAnswerSvc.create(dto);
        
     // QnA 상태를 '답변 완료'로 업데이트
        qnaAnswerSvc.updateQnAStateToAnswered(dto.getQnaPostId());
        
        log.info("save 결과: {}", entity);
        
        return ResponseEntity.ok(entity);
    }
    
//    @GetMapping("/all/{id}")
//    public ResponseEntity<Page<QnAAnswers>> getCommentsList(
//            @PathVariable(name = "id") Long id,
//            @RequestParam(name = "p", defaultValue = "0") int pageNo) {
//        log.info("getCommentList(id={}, pageNo={})", id, pageNo);
//        
//        Page<QnAAnswers> data = qnaAnswerSvc.readCommentsList(id, pageNo);
//        
//        return ResponseEntity.ok(data);
//    }
    
    @GetMapping("/all/{id}")
    public ResponseEntity<List<QnAAnswers>> getCommentsList(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        // QnA 게시글이 비밀글인지 확인
        QnA qna = qnaAnswerSvc.findQnAById(id);
        String signedInUser = (userDetails != null) ? userDetails.getUsername() : null;
        Integer userRole = (userDetails != null) ? ((User) userDetails).getUserRole() : null;

        // 비밀글인 경우, 작성자나 관리자만 댓글을 볼 수 있음
        if (qna.isSecret() && (signedInUser == null || (!qna.getQnaUserId().equals(signedInUser) && userRole != 0))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 접근 금지 응답
        }

        List<QnAAnswers> comments = qnaAnswerSvc.findByQnaId(id);
        return ResponseEntity.ok(comments);
    }
    
    @PreAuthorize("hasRole('ADMIN')")  
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteComments(@PathVariable(name = "id") Long id) {
        log.info("deleteComments(id={})", id);
        
        qnaAnswerSvc.delete(id);
        
        return ResponseEntity.ok(id);
    }
    
    @PreAuthorize("hasRole('ADMIN')") 
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateComments(@PathVariable(name = "id") Long id,
            @RequestBody QnAAnswerUpdateDto dto) {
        log.info("updateComments(id={}, dto={})", id, dto);
        
        log.info("Received update request for QnAAnswers with ID: {}", id);

        // Check if the entity exists
        Optional<QnAAnswers> entity = qnaAnswerSvc.findQnAAnsById(id);
        if (entity.isEmpty()) {
            log.error("QnAAnswers with ID: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        qnaAnswerSvc.update(dto);
        
        return ResponseEntity.ok(id);
    }
}
