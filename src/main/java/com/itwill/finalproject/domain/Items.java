package com.itwill.finalproject.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Items {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer itemId; // 물품 아이디
	
	@Basic(optional = false)
	private String itemName; // 물품 이름
	
	@Basic(optional = false)
	private Integer itemPrice; // 물품 가격
	
	private String itemImg; // 물품 사진 경로
	
	@Basic(optional = false)
	private String itemDesc; // 물품 설명
}
