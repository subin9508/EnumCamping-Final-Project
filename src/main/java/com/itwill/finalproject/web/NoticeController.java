package com.itwill.finalproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.finalproject.service.NoticeService;

import ch.qos.logback.core.model.Model;
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
		log.debug("GET : list");
		
//		List<NoticeListDto> list = noticeService.selectAllNotice();
//		log.debug("service 끝");
//		model.addAttribute("notices", list);
//		log.debug("model={}",model);
	}
	/*
	//공지사항 클릭 시 상세 내용 확인, 수정 화면
	@GetMapping({"/details","/modify"})
	public void noticeDetails(Model model, @RequestParam(name = "id") int id) {
		log.debug("GET : details");
		Notice notice = noticeService.selectNoticeById(id);
		
		//certify 부분이 필요가 없음 음.. 근데 굳이 dto 안 써도 될 듯 합니다
		//jsp에서 가져오는 것만 선별하면 됨. - 바꾸기
		NoticeDetailsDto dto = NoticeDetailsDto.fromEntity(notice);
		
		model.addAttribute("notice", dto); 
		log.debug("model에 추가 {}",dto);
		//return "community/notice/details";
	}
	
	//공지사항 작성 - jsp에서 admin계정 체크함
	@GetMapping("/create")
	public void noticeCreate(Notice notice) {
		log.debug("GET: create");
	}
	
	//공지사항 작성 제출
	@PostMapping("/create")
	public String insertNotice(NoticeCreateDto dto) {

		log.debug("POST: create(dto={})", dto);
		//제목이랑 내용만 insert함
		noticeService.insertNotice(dto);
		
		//추가된 공지사항이 포함된 list 화면으로 이동
		return "redirect:/community/notice/list";
	}
	
	
	//공지사항 삭제
	@GetMapping("/delete")
	public String noticedelete(@RequestParam(name = "notPostId") int notPostId) {
		log.debug("GET: delete");
		
		//postId로 공지사항 삭제
		noticeService.deleteNotice(notPostId);
		return "redirect:list";
	}
	
	//업데이트 버튼 클릭
	@GetMapping("/update")
	public void noticeUpdate() {
		log.debug("GET: update");
	}
	
	//업데이트
	@PostMapping("/update")
	public String noticeUpdate(NoticeUpdateDto dto){
		log.debug("POST: update");
		log.debug("{}",dto);
		noticeService.updateNotice(dto);
	
		return "redirect:list";
	}
	
	//검색 기능. dto이용
    @GetMapping("/search")
    public String search(NoticeSearchDto dto, Model model) {
    	log.debug("search(dto = {})",dto);
    	
    	List<NoticeListDto> list = noticeService.search(dto);
    	//검색 결과를 notice로 다시 model에 추가하므로 redirect 필요 X
    	model.addAttribute("notices",list);
    	
    	return "/community/notice/list";
    }
	*/
	

}    