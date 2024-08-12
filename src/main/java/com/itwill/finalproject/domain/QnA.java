package com.itwill.finalproject.domain;

import java.sql.Date;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="QNA")
public class QnA extends BaseTimeEntity {
	
	@Id // PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) //generated as identity
	@Column(name = "QNA_POST_ID")
	private Long id;
	
	@Basic(optional = false) // not null
	@Column(name = "QNA_TITLE")
	private String title;
	
	@Basic(optional = false) // not null
	@Column(name = "QNA_CONTENT")
	private String content;
	
	@Basic(optional = false) // not null
	@Column(name = "QNA_USER_ID")
	private String author;
	
	private Integer qna_view_cnt;
		
	private Integer qna_state;
	
	private Integer qna_lock;
	
    // update 기능(제목/내용 수정)에서 사용할 공개 메서드
    public QnA update(String title, String content) {
        this.title = title;
        this.content = content; 
        
        return this;
    }
}
