package com.itwill.finalproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/introduce")
public class IntroduceController {
	
	@GetMapping("/map")
	public String map() {
		log.debug("map()");
		return "/introduce/map";
	
	}
	
	@GetMapping("/enumIntro")
	public String enumIntro() {
		log.debug("enumIntro()");
		
		return "/introduce/enumIntro";
	}
	
	@GetMapping("/facilityLayout")
	public String facilityLayout() {
		log.debug("facilityLayout()");
		
		return "/introduce/facilityLayout";
	}
		
	@GetMapping("/facilities")
	public String facilities() {
		log.debug("facilities()");
		return "/introduce/facilities";
	
	}
	
	@GetMapping("/travel")
	public String travel() {
		log.debug("travel()");
		return "/introduce/travel";
	
	}


}
