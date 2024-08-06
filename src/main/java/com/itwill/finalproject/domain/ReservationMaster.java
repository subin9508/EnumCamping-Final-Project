package com.itwill.finalproject.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class ReservationMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer resId;
	
	private String userId;
	
	private String requirement;
	
	private String resCreatedTime;
	
	private LocalDateTime resModifiedTime;
	
	private LocalDate resCheckIn;
	
	private LocalDate resCheckOut;
	
	private Integer resTotalPrice;
	
	private Integer resState;
}
