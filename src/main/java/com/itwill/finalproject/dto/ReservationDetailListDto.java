package com.itwill.finalproject.dto;


import com.itwill.finalproject.domain.ReservationDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDetailListDto {

//	private Integer detailId;
	private Integer itemId;
	private Integer itemQuantity;
	private Integer itemAmount;
//	private String itemName;  
//  private String itemImg;
	
	public ReservationDetail fromEntity(ReservationDetail reservationDetail) {
		return ReservationDetail.builder()
				.reservationMaster(reservationDetail.getReservationMaster())
				.item(reservationDetail.getItem())
				.itemQuantity(reservationDetail.getItemQuantity())
				.itemAmount(reservationDetail.getItemAmount())
				.build();
	}
}
