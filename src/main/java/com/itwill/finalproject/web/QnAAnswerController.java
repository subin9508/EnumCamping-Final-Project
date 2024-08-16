package com.itwill.finalproject.web;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.finalproject.domain.QnAAnswers;
import com.itwill.finalproject.dto.QnAAnswerRegisterDto;
import com.itwill.finalproject.dto.QnAAnswerUpdateDto;
import com.itwill.finalproject.service.QnAAnswerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/qnAAnswers")
public class QnAAnswerController {

    private final QnAAnswerService qnaAnswerSvc;
    
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ResponseEntity<QnAAnswers> registerComments(@RequestBody QnAAnswerRegisterDto dto) {
        log.info("registerComments(dto={})", dto);
        
        QnAAnswers entity = qnaAnswerSvc.create(dto);
        log.info("save 결과: {}", entity);
        
        return ResponseEntity.ok(entity);
    }
    
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/all/{id}")
    public ResponseEntity<Page<QnAAnswers>> getCommentsList(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "p", defaultValue = "0") int pageNo) {
        log.info("getCommentList(id={}, pageNo={})", id, pageNo);
        
        Page<QnAAnswers> data = qnaAnswerSvc.readCommentsList(id, pageNo);
        
        return ResponseEntity.ok(data);
    }
    
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteComments(@PathVariable Long id) {
        log.info("deleteComments(id={})", id);
        
        qnaAnswerSvc.delete(id);
        
        return ResponseEntity.ok(id);
    }
    
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateComments(@PathVariable Long id,
            @RequestBody QnAAnswerUpdateDto dto) {
        log.info("updateComments(id={}, dto={})", id, dto);
        
        qnaAnswerSvc.update(dto);
        
        return ResponseEntity.ok(id);
    }
}
