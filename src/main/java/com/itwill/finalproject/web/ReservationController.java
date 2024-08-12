package com.itwill.finalproject.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
	
	private final ReservationService reservationSvc;
	
	@GetMapping("/calendar")
	public void reservationCalendar() {
		log.info("reservationCalendar");
		
	}
	
	@GetMapping("/calendar/{date}")
	@ResponseBody
	public List<Integer> reservationCalendar(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		log.debug("GET: calendar with date {}", date);
		
		 // 해당 날짜에 예약된 구역 ID 목록을 가져옵니다.
        List<Integer> reservedAreaIds = reservationSvc.readReservedAreas(date);
        return reservedAreaIds;
	}
	
	@GetMapping("/calendar/{date}/{area}")
	public ResponseEntity<List<ReservationMaster>> reservationCalendar(@PathVariable("date") String date, @PathVariable("area") int area) {
		LocalDate checkInDate = LocalDate.parse(date);
		log.debug("GET: calendar with date and area {}, {}", date, area);
		List<ReservationMaster> reservations = reservationSvc.readReservationMaster(checkInDate, area);
		
		return new ResponseEntity<>(reservations, HttpStatus.OK);
	}

}
