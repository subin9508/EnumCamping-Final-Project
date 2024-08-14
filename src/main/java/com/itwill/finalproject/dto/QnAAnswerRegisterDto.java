package com.itwill.finalproject.dto;

import lombok.Data;

@Data
public class QnAAnswerRegisterDto {
	private Long qnaPostId;
	private String content;
	private String userId;
}
