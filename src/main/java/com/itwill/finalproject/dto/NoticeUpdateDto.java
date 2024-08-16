package com.itwill.finalproject.dto;

import java.time.LocalDateTime;

import com.itwill.finalproject.domain.Notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor //이거추가함
public class NoticeUpdateDto {
	private int id;
	private String title;
	private String content;
//	private LocalDateTime modifiedTime;
	
	//update는 createdTime 바꾸지 않음
	
	//Notice에 dto 내용 넣기
//	 public Notice toEntity() {
//		 return Notice.builder().title(title).content(content)
//				 .modifiedTime(modifiedTime).id(id).build();
//	 }
	 
		//Notice에 dto 내용 넣기
	 public Notice toEntity() {
		 return Notice.builder().title(title).content(content)
				 .id(id).build();
	 }
	
}
