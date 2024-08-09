package com.itwill.finalproject.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itwill.finalproject.domain.Notice;
import com.itwill.finalproject.dto.NoticeCreateDto;
import com.itwill.finalproject.dto.NoticeListDto;
import com.itwill.finalproject.dto.NoticeSearchDto;
import com.itwill.finalproject.dto.NoticeUpdateDto;
import com.itwill.finalproject.repository.NoticeRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class NoticeService {
	
	private NoticeRepository notRepo;
	
	public List<NoticeListDto> selectAllNotice(){
		List<Notice> list = notRepo.findAll();
		log.debug("list={}",list);
		return list.stream().map(NoticeListDto::fromEntity).toList();
	}
	
	public Notice selectNoticeById(int id){
		log.debug("selectNoticeById");
		Notice notice = notRepo.findById(id).orElseThrow();
		log.debug("notice={}",notice);
		return notice;
	}
	
	public Notice insertNotice(NoticeCreateDto dto) {
		log.debug("insertNotice");
		Notice result = notRepo.save(dto.toEntity());
		
		return result;
	};
	
	public void deleteNotice(int id) {
		log.debug("deleteNotice, id={}",id);
		notRepo.deleteById(id);
	}
	
	public int updateNotice(NoticeUpdateDto dto) {
		log.debug("updateNotice, {}",dto);
		Notice notice = notRepo.findById(dto.getId()).orElseThrow();
		notice.update(dto.getTitle(), dto.getContent(), dto.getModifiedTime());
		return 0;
	}
/*
	public List<NoticeListDto> search(NoticeSearchDto dto) {
		log.debug("search()");
		
		List<Notice> list =  dao.search(dto); //sql문장 만들어야함
		
		return list.stream().map(NoticeListDto::fromEntity).toList();
	}
	*/
}
