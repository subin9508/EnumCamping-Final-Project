package com.itwill.finalproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // 디스패쳐 서블릿에서 매서드를 호출할 컨트롤러 컴포넌트.
@RequestMapping("/info")
public class InfoController {
	
		@GetMapping("/")
		public String showinfo() {
			return "info/infoTime";
		}
		
		@GetMapping("/infoTime")
		public String showinfoTime() {
			return "info/infoTime"; // info/infoTime.jsp 뷰 이름
		}
		
		@GetMapping("/infoRefound")
		public String showinfoRefound() {
			return "info/infoRefound"; // info/infoRefund.jsp 뷰 이름

		}
		
	    @GetMapping("/infoBooking")
	    public String showBooking() {
	        return "info/infoBooking"; // info/infoBooking.jsp 뷰 이름
	    }

	    @GetMapping("/infoRules")
	    public String showRules() {
	        return "info/infoRules"; // info/infoRules.jsp 뷰 이름
	    }

	    @GetMapping("/infoSale")
	    public String showSale() {
	        return "info/infoSale"; // info/infoSale.jsp 뷰 이름
	    }

	    @GetMapping("/infoRental")
	    public String showRental() {
	        return "info/infoRental"; // info/infoRental.jsp 뷰 이름
	    }
	}