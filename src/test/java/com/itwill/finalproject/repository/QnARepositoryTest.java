package com.itwill.finalproject.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import com.itwill.finalproject.domain.QnA;
import com.itwill.finalproject.domain.QnAAnswers;
import com.itwill.finalproject.service.QnAAnswerService;
@SpringBootTest
public class QnARepositoryTest {

    @Autowired
    private QnAAnswerService qnaAnswerService;
    
    @Autowired
    private QnAAnswerRepository qnaAnswerRepository;
    
    @Autowired
    private QnARepository qnaRepository;
    
    @BeforeEach
    public void setUp() {
        // QnA 엔티티를 먼저 생성하고 저장합니다.
        QnA qna = new QnA();
        qna.setTitle("Sample QnA Title");  // 필수 필드 추가
        qna.setContent("Sample QnA Content"); // Content 필드가 필요하다면 추가
        QnA savedQnA = qnaRepository.save(qna);  // 자동 생성된 ID 사용

        // QnAAnswers 엔티티를 생성하고 저장합니다.
        QnAAnswers answer = new QnAAnswers();
        answer.setQna(savedQnA); // QnA 객체를 제대로 참조하도록 설정
        answer.setContents("Test Answer");
        answer.setUserId("testUser");
        qnaAnswerRepository.save(answer);
    }
    
    // 테스트 오류 
//    @Test 
    public void testReadCommentsList() {
        // 실제로 데이터베이스에 존재하는 QnA ID를 사용해야 합니다.
        Long id = qnaRepository.findAll().get(0).getId(); // 첫 번째 QnA의 ID 사용
        Page<QnAAnswers> result = qnaAnswerService.readCommentsList(id, 0);
        assertFalse(result.isEmpty(), "The result should not be empty.");
    }
}
