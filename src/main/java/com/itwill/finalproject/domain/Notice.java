package com.itwill.finalproject.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "notices")
public class Notice {
	
	@Id
	private Integer notPostId;
	
	private String notTitle;
	
	private String notContent;
	
	private LocalDateTime notCreatedTime;
	
	private LocalDateTime notModifiedTime;
	
	private Integer notCertify;
}
