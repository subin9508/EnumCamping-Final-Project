package com.itwill.finalproject.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ReservationUpdateDto {
	private Integer resId;
	private LocalDate newCheckIn;
	private LocalDate newCheckOut;
	private String newRequirement;
	private List<ReservationItemUpdateDto> updatedItems;
}

