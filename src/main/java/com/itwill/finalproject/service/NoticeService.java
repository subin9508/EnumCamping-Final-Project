package com.itwill.finalproject.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itwill.finalproject.domain.Notice;
import com.itwill.finalproject.dto.NoticeCreateDto;
import com.itwill.finalproject.dto.NoticeListDto;
import com.itwill.finalproject.dto.NoticeSearchDto;
import com.itwill.finalproject.dto.NoticeUpdateDto;
import com.itwill.finalproject.repository.NoticeRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class NoticeService {
	
	private NoticeRepository notRepo;
	
	public List<NoticeListDto> selectAllNotice(){
		List<Notice> list = notRepo.findAllByOrderByIdDesc();
		log.info("list={}",list);
		return list.stream().map(NoticeListDto::fromEntity).toList();
	}
	
	public Notice selectNoticeById(int id){
		log.info("selectNoticeById");
		Notice notice = notRepo.findById(id).orElseThrow();
		log.info("notice={}",notice);
		return notice;
	}
	
	public Notice insertNotice(NoticeCreateDto dto) {
		log.info("insertNotice");
		Notice result = notRepo.save(dto.toEntity());
		
		return result;
	};
	
	public void deleteNotice(int id) {
		log.info("deleteNotice, id={}",id);
		notRepo.deleteById(id);
	}
	
	@Transactional
	public Notice updateNotice(NoticeUpdateDto dto) {
		log.info("updateNotice, {}",dto);
		Notice notice  = notRepo.findById(dto.getId()).orElseThrow();
		notice.update(dto.getTitle(), dto.getContent());
		return notice;
	}

	public List<NoticeListDto> search(NoticeSearchDto dto) {
		log.info("search(dto={})",dto);
		
		List<Notice> list =  notRepo.searchByCategory(dto);
		
		return list.stream().map(NoticeListDto::fromEntity).toList();
	}
	
}
