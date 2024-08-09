package com.itwill.finalproject.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.finalproject.domain.Notice;
import com.itwill.finalproject.dto.NoticeListDto;
import com.itwill.finalproject.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@RequestMapping("/community/notice")
@RequiredArgsConstructor //의존성주입.
public class NoticeController {
	private final NoticeService noticeService; 
	
	//공지사항 전체 목록 확인
	@GetMapping("/list")
	public void noticeList(Model model) {
		log.info("GET : list");
		
		List<NoticeListDto> list = noticeService.selectAllNotice();
		log.info("list = {}",list);
		model.addAttribute("notices", list);
		log.info("model={}",model);
	}
	
	//공지사항 클릭 시 상세 내용 확인, 수정 화면
	@GetMapping({"/details","/modify"})
	public void noticeDetails(Model model, @RequestParam(name = "id") int id) {
		log.info("GET : details");
		Notice notice = noticeService.selectNoticeById(id);
		
		model.addAttribute("notice", notice); 
		log.info("model에 추가 {}",notice);
		//return "community/notice/details";
	}
	/*
	//공지사항 작성 - jsp에서 admin계정 체크함
	@GetMapping("/create")
	public void noticeCreate(Notice notice) {
		log.info("GET: create");
	}
	
	//공지사항 작성 제출
	@PostMapping("/create")
	public String insertNotice(NoticeCreateDto dto) {

		log.info("POST: create(dto={})", dto);
		//제목이랑 내용만 insert함
		noticeService.insertNotice(dto);
		
		//추가된 공지사항이 포함된 list 화면으로 이동
		return "redirect:/community/notice/list";
	}
	
	
	//공지사항 삭제
	@GetMapping("/delete")
	public String noticedelete(@RequestParam(name = "notPostId") int notPostId) {
		log.info("GET: delete");
		
		//postId로 공지사항 삭제
		noticeService.deleteNotice(notPostId);
		return "redirect:list";
	}
	
	//업데이트 버튼 클릭
	@GetMapping("/update")
	public void noticeUpdate() {
		log.info("GET: update");
	}
	
	//업데이트
	@PostMapping("/update")
	public String noticeUpdate(NoticeUpdateDto dto){
		log.info("POST: update");
		log.info("{}",dto);
		noticeService.updateNotice(dto);
	
		return "redirect:list";
	}
	
	//검색 기능. dto이용
    @GetMapping("/search")
    public String search(NoticeSearchDto dto, Model model) {
    	log.info("search(dto = {})",dto);
    	
    	List<NoticeListDto> list = noticeService.search(dto);
    	//검색 결과를 notice로 다시 model에 추가하므로 redirect 필요 X
    	model.addAttribute("notices",list);
    	
    	return "/community/notice/list";
    }
	*/
	

}    