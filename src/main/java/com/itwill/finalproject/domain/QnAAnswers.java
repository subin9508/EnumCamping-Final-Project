package com.itwill.finalproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="QNAANSWERS")
public class QnAAnswers extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 번호
	
    @ToString.Exclude // toString 메서드를 만들 때 제외시킴.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QNA_POST_ID") // FK 제약조건이 있는 컬럼 이름.
    private QnA qna;
	
    private String contents; // 내용
    
    private String userId; // 작성자
    
    public QnAAnswers update(String contents) {
        this.contents = contents;
        return this;
    }
}