package com.itwill.finalproject.domain;

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
	
	private String itemName; // 물품 이름
	
	private Integer itemPrice; // 물품 가격
	
	private String itemImg; // 물품 사진 경로
	
	private String itemDesc; // 물품 설명
}
