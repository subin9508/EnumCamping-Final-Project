package com.itwill.finalproject.web;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.finalproject.dto.QnAListItemdto;
import com.itwill.finalproject.service.QnAService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/community/qna")
public class QnAController {
	
	private final QnAService qnaSvc;
		
	@GetMapping("/list")
	public void list(@RequestParam(name = "p", defaultValue = "0" ) int pageNo, Model model) {
		log.info("list(pageNo={}", pageNo);
		
		
	}
		
}