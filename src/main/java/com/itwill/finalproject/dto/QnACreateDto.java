package com.itwill.finalproject.dto;

import com.itwill.finalproject.domain.QnA;

import lombok.Data;

@Data
public class QnACreateDto {
	private String title;
	private String content;
	private String qnaUserId;
	private Integer qnaLock;
	
	public QnA toEntity() {
		return QnA.builder()
				.title(title)
				.content(content)
				.qnaUserId(qnaUserId)
				.qnaViewCnt(0) // 글 작성 시 조회수를 0으로 초기화
				.qnaLock(qnaLock)
				.build();
	}
}
