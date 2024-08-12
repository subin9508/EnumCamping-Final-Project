package com.itwill.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationDetailDto {
	
	private Integer resId;
	 private Integer itemId;
	 private Integer itemQuantity;
	 private Integer itemAmount;
	 private String itemName;
	 private String itemImg;

	 public ReservationDetailDto(Integer resId, Integer itemId, Integer itemQuantity, Integer itemAmount, String itemName, String itemImg) {
		 this.resId = resId;
		 this.itemId = itemId;
		 this.itemQuantity = itemQuantity;
		 this.itemAmount = itemAmount;
		 this.itemName = itemName;
		 this.itemImg = itemImg;
	 }
}
