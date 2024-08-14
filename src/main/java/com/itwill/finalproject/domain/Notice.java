package com.itwill.finalproject.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "notices")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notice extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "not_post_id")
	private Integer id;
	
	@Basic(optional = false)
	@Column(name = "not_title")
	private String title;
	
	@Basic(optional = false)
	@Column(name = "not_content")
	private String content;
	
//	@CreatedDate
//	@Column(name = "not_created_time")
//	private LocalDateTime createdTime;
//	
//	@LastModifiedDate
//	@Column(name = "not_modified_time")
//	private LocalDateTime modifiedTime;
	
	
	
	//title& content는 update 기능이 되어야함. //-> 그래서 메서드 만듬
	//update 기능(제목, 내용 수정 기능) 에서 사용할 공개 메서드
	public Notice update(String title, String content) {
		this.title = title;
		this.content = content;
//		this.modifiedTime = modifiedTime;
		
		return this;//-> 자기자신 리턴.(필드 초기화해서 리턴함)
	}
	
	
}
