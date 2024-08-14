package com.itwill.finalproject.dto;

import java.time.LocalDateTime;

import com.itwill.finalproject.domain.Notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data  @Builder @AllArgsConstructor
public class NoticeListDto {

	private Integer id;
	private String title;
	private LocalDateTime modifiedTime;
	
	//전체 목록 & 검색한 목록 표시할 때 사용
	
	//Notice에서 필요한 내용 뽑아오기
	public static NoticeListDto fromEntity(Notice notice) {
		return NoticeListDto.builder().id(notice.getId())
				.modifiedTime(notice.getModifiedTime())
				.title(notice.getTitle()).build();
	}
	
	
	
}
