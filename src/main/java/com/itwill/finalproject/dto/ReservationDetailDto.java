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
	 
	 public ReservationDetailDto(Integer itemId, Integer itemQuantity, Integer itemAmount) {
		    this.itemId = itemId;
		    this.itemQuantity = itemQuantity;
		    this.itemAmount = itemAmount;
		}
}
