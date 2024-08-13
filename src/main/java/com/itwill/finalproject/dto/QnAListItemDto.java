package com.itwill.finalproject.dto;

import java.time.LocalDateTime;

import com.itwill.finalproject.domain.QnA;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder
public class QnAListItemDto {
	private Long id;
	private String title;
	private String qnaUserId;
	private LocalDateTime modifiedTime;
	private Integer qnaLock;
	private Integer qnaState;
	private Integer qnaViewCnt;
	
	public static QnAListItemDto fromEntity(QnA entity) {
		return QnAListItemDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.qnaUserId(entity.getQnaUserId())
				.modifiedTime(entity.getModifiedTime())
				.qnaLock(entity.getQnaLock())
				.qnaState(entity.getQnaState())
				.qnaViewCnt(entity.getQnaViewCnt())
				.build();
	}
}
