package com.itwill.finalproject.service;

import org.springframework.stereotype.Service;

import com.itwill.finalproject.repository.NoticeRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class NoticeService {
	
	private NoticeRepository notRepo;
	/*
	public List<NoticeListDto> selectAllNotice(){
		List<Notice> list = dao.selectAllNotice();
		log.debug("list={}",list);
		return list.stream().map(NoticeListDto::fromEntity).toList();
	}
	
	public Notice selectNoticeById(int id){
		log.debug("selectNoticeById");
		Notice notice = dao.selectNoticeById(id);
		log.debug("notice={}",notice);
		return notice;
	}
	
	public int insertNotice(NoticeCreateDto dto) {
		log.debug("insertNotice");
		int result = dao.insertNotice(dto.toEntity());
		
		return result;
	};
	
	public int deleteNotice(int id) {
		log.debug("deleteNotice, id={}",id);
		int result = dao.deleteNotice(id);
		return result;
	}
	
	public int updateNotice(NoticeUpdateDto dto) {
		log.debug("updateNotice, {}",dto);
		int result = dao.updateNotice(dto.toEntity());
		return result;
	}

	public List<NoticeListDto> search(NoticeSearchDto dto) {
		log.debug("search()");
		
		List<Notice> list =  dao.search(dto);
		
		return list.stream().map(NoticeListDto::fromEntity).toList();
	}
	*/
}
