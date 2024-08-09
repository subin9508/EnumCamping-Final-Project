package com.itwill.finalproject.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.repository.ReservationMasterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {
	private final ReservationMasterRepository reservationMasterRepo;
	
	// 특정 날짜에 예약된 지역을 읽기
	public List<Integer> readReservedAreas(LocalDate date) {
	    LocalDate minusCheckIn = date.minusDays(1);
		
		return reservationMasterRepo.selectByResCheckIn(minusCheckIn);
	}
	
	// 특정 날짜와 지역에 해당하는 예약 마스터 정보를 읽어오기
	public List<ReservationMaster> readReservationMaster(LocalDate date, int area) {
		LocalDate plusCheckIn = date.plusDays(1);
		
		return reservationMasterRepo.selectByItemIdAndResCheckIn(area, plusCheckIn);
	}
}
