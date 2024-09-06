package com.itwill.finalproject.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.ClaimDateDetail;
import com.itwill.finalproject.domain.ClaimDetail;
import com.itwill.finalproject.domain.ClaimMaster;
import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.dto.ClaimDetailDto;
import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.repository.ClaimDateDetailRepository;
import com.itwill.finalproject.repository.ClaimDetailRepository;
import com.itwill.finalproject.repository.ClaimMasterRepository;
import com.itwill.finalproject.repository.ItemsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClaimService {

	private final ClaimMasterRepository clmMasterRepo;
	private final ClaimDetailRepository clmDetailRepo;
	private final ClaimDateDetailRepository clmDateDetailRepo;
	private final ItemsRepository itemsRepo;

	@Transactional
	public void saveClaim(ReservationMaster resMaster, List<ReservationDetailDto> resDetail, Integer resId) {
	    // ClaimMaster 생성 및 저장
		ClaimMaster claimMaster = new ClaimMaster();
		claimMaster.setResId(resId); // resId를 반드시 설정
		claimMaster.setReason("예약 변경");
		claimMaster.setCancelTime(LocalDateTime.now());
		claimMaster.setTotalPrice(resMaster.getResTotalPrice());
		claimMaster.setClmChange("체크인 날짜: " + resMaster.getResCheckIn() + " 체크아웃 날짜: " + resMaster.getResCheckOut());


	    log.debug("Before save: claimMaster={}", claimMaster);

	    // ClaimMaster를 저장하여 clmId가 생성되도록 합니다.
	    claimMaster = clmMasterRepo.save(claimMaster);

	    log.debug("After save: clmId={}, resId={}", claimMaster.getClmId(), claimMaster.getResId());

	    if (claimMaster.getClmId() == null) {
	        log.error("clmId is still null after saving ClaimMaster. This will cause a NullPointerException.");
	        throw new IllegalStateException("clmId was not generated properly.");
	    }
	    	    
	    ClaimDateDetail claimDateDetail = new ClaimDateDetail();
	    claimDateDetail.setClmId(claimMaster.getClmId());
	    claimDateDetail.setCanCheckIn(resMaster.getResCheckIn());
	    claimDateDetail.setCanCheckOut(resMaster.getResCheckOut());

	    for (ReservationDetailDto detail : resDetail) {
	    	Items item = itemsRepo.findById(detail.getItemId())
	                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + detail.getItemId()));
	    	
	        ClaimDetail claimDetail = new ClaimDetail();
	        claimDetail.setClmId(claimMaster.getClmId());
	        claimDetail.setResId(resMaster.getResId());
	        claimDetail.setItem(item);
	        claimDetail.setItemQuantity(detail.getItemQuantity());
	        claimDetail.setItemAmount(detail.getItemAmount());
	        claimDetail.setItemChange("아이템 수량 변경");
	        clmDetailRepo.save(claimDetail);
	        
	        if (detail.getItemId() >= 20) {
	        	claimDateDetail.setPrice(detail.getItemAmount());
	        }
	    }
	    
	    clmDateDetailRepo.save(claimDateDetail);

	}
	
	@Transactional
	public ClaimMaster findByResIdMaxClmId(Integer resId) {
		return clmMasterRepo.findByResIdMaxClmId(resId);
	}
	
	public List<ClaimDetailDto> findByClaimMasterId(Integer clmId) {
        // ClaimDetail 엔티티 리스트를 가져옴
        List<ClaimDetail> claimDetails = clmDetailRepo.findByClmId(clmId);

        // ClaimDetail을 ClaimDetailDto로 변환
        return claimDetails.stream().map(claimDetail -> {
            ClaimDetailDto dto = new ClaimDetailDto();
            dto.setCdId(claimDetail.getCdId());
            dto.setClmId(claimDetail.getClmId());
            dto.setResId(claimDetail.getResId());
            dto.setItemId(claimDetail.getItem().getItemId()); // 연관된 Items 엔티티에서 itemId 가져옴
            dto.setItemName(claimDetail.getItem().getItemName()); // 연관된 Items 엔티티에서 itemName 가져옴
            dto.setItemQuantity(claimDetail.getItemQuantity());
            dto.setItemAmount(claimDetail.getItemAmount());
            dto.setItemImg(claimDetail.getItem().getItemImg()); // Items 엔티티에서 itemImg 가져옴
            return dto;
        }).toList();
    }
	
	@Transactional
	public ClaimDateDetail getClaimDateDetailByClmId(Integer clmId) {
		return clmDateDetailRepo.findByClmId(clmId);
	}
	
	@Transactional
	public List<ClaimMaster> findByResId(Integer resId) {
	    return clmMasterRepo.findByResId(resId);
	}

}
