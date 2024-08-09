package com.itwill.finalproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor()
@RequestMapping("/reservation")
public class ReservationController {
	
	@GetMapping("/calendar")
	public void reservationCalendar() {
		log.info("reservationCalendar");
		
	}

}
