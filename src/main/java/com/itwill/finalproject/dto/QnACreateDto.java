package com.itwill.finalproject.dto;

import com.itwill.finalproject.domain.QnA;

import lombok.Data;

@Data
public class QnACreateDto {
	private String title;
	private String content;
	private String qnaUserId;
	
	public QnA toEntity() {
		return QnA.builder()
				.title(title)
				.content(content)
				.qnaUserId(qnaUserId)
				.build();
	}
}
