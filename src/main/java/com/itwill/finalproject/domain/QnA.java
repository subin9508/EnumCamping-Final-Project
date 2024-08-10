package com.itwill.finalproject.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="QNA")
public class QnA extends BaseTimeEntity {
	
	@Id // PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) //generated as identity
	private Long id;
	
	@Basic(optional = false) // not null
	private String title;
	
	@Basic(optional = false) // not null
	private String content;
	
	@Basic(optional = false) // not null
	private String author;
	
	private Integer view_cnt;
	
	private Integer state;
	
	private Integer lock;
}
