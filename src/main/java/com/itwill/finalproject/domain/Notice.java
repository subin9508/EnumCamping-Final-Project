package com.itwill.finalproject.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Notice {
	
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
	
	@Column(name = "not_created_time")
	private LocalDateTime createdTime;
	
	@Column(name = "not_modified_time")
	private LocalDateTime modifiedTime;
	
	private Integer notCertify;
}
