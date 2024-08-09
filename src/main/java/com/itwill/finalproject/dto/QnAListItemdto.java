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
public class QnAListItemdto {
	private Long id;
	private String title;
	private String author;
	private LocalDateTime modifiedTime;
	
	public static QnAListItemdto fromEntity(QnA entity) {
		return QnAListItemdto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.author(entity.getAuthor())
				.modifiedTime(entity.getModifiedTime())
				.build();
	}
}
