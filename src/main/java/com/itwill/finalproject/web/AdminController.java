package com.itwill.finalproject.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.service.AdminService;
import com.itwill.finalproject.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final ReservationService reservationSvc;
	private final AdminService adminSvc;
	
	
	@GetMapping("/price/zones")
	public void zonePrice(Model model) {
		log.info("zonePrice");
		List<Items> items = reservationSvc.getAllZones();
		model.addAttribute("items", items);
	}
	
	@GetMapping("/price/items")
	public void itemPrice(Model model) {
		log.info("itemPrice");
		List<Items> items = reservationSvc.getAllItems();
		model.addAttribute("items", items);
	}
	
	
	@PostMapping("/price/update")
	public String priceUpdate(@RequestParam("redirectUrl") String redirectUrl) {
	    // Perform the necessary update logic here

	    // Redirect to the URL passed as a parameter
	    return "redirect:" + redirectUrl;
	}


}
