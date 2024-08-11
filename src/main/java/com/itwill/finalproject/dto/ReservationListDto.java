package com.itwill.finalproject.dto;

import java.time.LocalDate;

import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationListDto {
	
	private String userId;
	private Integer resId;
	private LocalDate resCheckIn;
	private Integer resState;
	private String resCreatedTime;	
	private Integer itemId;
	
	public ReservationListDto fromEntity(ReservationMaster reservationMaster, ReservationDetail detail) {
		return ReservationListDto.builder()
				.userId(reservationMaster.getUser().getUserId())
				.resId(reservationMaster.getResId())
				.resCheckIn(reservationMaster.getResCheckIn())
				.resState(reservationMaster.getResState())
				.resCreatedTime(reservationMaster.getResCreatedTime())
				.itemId(detail.getItem().getItemId())
				.build();
	}

}
