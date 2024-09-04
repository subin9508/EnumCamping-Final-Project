package com.itwill.finalproject.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.ClaimDetail;
import com.itwill.finalproject.domain.ClaimMaster;
import com.itwill.finalproject.domain.ClaimMasterId;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.repository.ClaimDetailRepository;
import com.itwill.finalproject.repository.ClaimMasterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClaimService {

	private final ClaimMasterRepository clmMasterRepo;
	private final ClaimDetailRepository clmDetailRepo;

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

	    for (ReservationDetailDto detail : resDetail) {
	        ClaimDetail claimDetail = new ClaimDetail();
	        claimDetail.setClmId(claimMaster.getClmId());
	        claimDetail.setResId(resMaster.getResId());
	        claimDetail.setItemId(detail.getItemId());
	        claimDetail.setItemQuantity(detail.getItemQuantity());
	        claimDetail.setItemAmount(detail.getItemAmount());
	        claimDetail.setItemChange("아이템 수량 변경");
	        clmDetailRepo.save(claimDetail);
	    }
	}
}

