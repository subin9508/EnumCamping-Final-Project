package com.itwill.finalproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDetail {
	
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RES_ID")
	private Integer resId; // 예약 아이디
	
	private Integer itemId; // 아이템 아이디
	
	private Integer itemQuantity; // 아이템 수량
	
	private Integer itemAmount; // 아이템 가격(단가 * 수량)
	
	private String itemName; // 아이템 이름
	
	private String itemImg; // 아이템 사진
}
