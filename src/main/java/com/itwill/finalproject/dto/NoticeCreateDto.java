package com.itwill.finalproject.dto;



import com.itwill.finalproject.domain.Notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class NoticeCreateDto {
	//제목이랑 내용만 있으면 됨. 생성시간은 자동
	private String title;
	private String content;
	
	//Notice에 DTO 내용 넣기
	public Notice toEntity() {
		return Notice.builder()
				.content(content)
				.title(title).build();
	}
}
