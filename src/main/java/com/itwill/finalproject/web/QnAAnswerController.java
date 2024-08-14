package com.itwill.finalproject.web;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.finalproject.domain.QnAAnswer;
import com.itwill.finalproject.dto.QnAAnswerRegisterDto;
import com.itwill.finalproject.dto.QnAAnswerUpdateDto;
import com.itwill.finalproject.service.QnAAnswerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/QnAAnswer")
public class QnAAnswerController {

    private final QnAAnswerService qnaAnswerSvc;
    
//    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<QnAAnswer> registerComment(@RequestBody QnAAnswerRegisterDto dto) {
        log.info("registgerComment(dto={})", dto);
        
        // 서비스 계층의 메서드 호출(댓글 등록 서비스 실행)
        QnAAnswer entity = qnaAnswerSvc.create(dto);
        log.info("save 결과: {}", entity);
        
        return ResponseEntity.ok(entity);
    }
    
//    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all/{qnaPostId}")
    public ResponseEntity<Page<QnAAnswer>> getCommentList(
            @PathVariable(name = "qnaPostId") Long qnaPostId,
            @RequestParam(name = "p", defaultValue = "0") int pageNo) {
        log.info("getCommentList(qnaPostId={}, pageNo={})", qnaPostId, pageNo);
        
        Page<QnAAnswer> data = qnaAnswerSvc.readCommentList(qnaPostId, pageNo);
        
        return ResponseEntity.ok(data);
    }
    
//    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteComment(@PathVariable Long id) {
        log.info("deleteComment(id={})", id);
        
        qnaAnswerSvc.delete(id);
        
        return ResponseEntity.ok(id); // 삭제한 댓글 아이디를 응답으로 보냄.
    }
    
//    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateComment(@PathVariable Long id,
            @RequestBody QnAAnswerUpdateDto dto) {
        log.info("updateComment(id={}, dto={})", id, dto);
        
        qnaAnswerSvc.update(dto);
        
        return ResponseEntity.ok(id); // 업데이트한 댓글의 아이디를 응답으로 보냄.
    }
}
    
