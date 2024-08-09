package com.itwill.finalproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/intro")
public class IntroController {
	
	@GetMapping("/map")
	public String map() {
		log.debug("map()");
		return "/intro/map";
	
	}
	
	@GetMapping("/facilities")
	public String facilities() {
		log.debug("facilities()");
		return "/intro/facilities";
	
	}
	
	@GetMapping("/travel")
	public String travel() {
		log.debug("travel()");
		return "/intro/travel";
	
	}

}
