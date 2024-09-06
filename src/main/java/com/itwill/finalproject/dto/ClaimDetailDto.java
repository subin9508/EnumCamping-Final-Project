package com.itwill.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDetailDto {
	private Integer cdId;
	private Integer clmId;
	private Integer resId;
	private Integer itemId;
	private Integer itemQuantity;
	private Integer itemAmount;
	private String itemName;
	private String itemImg;
	
}
