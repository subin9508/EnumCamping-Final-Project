package com.itwill.finalproject.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.dto.ReservationMasterDto;
import com.itwill.finalproject.dto.UserUpdateDto;
import com.itwill.finalproject.repository.ReservationDetailRepository;
import com.itwill.finalproject.repository.QnARepository;
import com.itwill.finalproject.repository.ReservationMasterRepository;

import com.itwill.finalproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {
	
	private final UserRepository userRepo;
    private final ReservationMasterRepository reservationMasterRepo;
    private final ReservationDetailRepository reservationDetailRepo;

	
	// 사용자 정보 조회
	public User read(String userId) {
		log.debug("read(id={})", userId);
		
		// 사용자 ID를 이용해 사용자 정보를 조회
		return userRepo.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
		
	}

	// 사용자 정보 업데이트
	public void update(UserUpdateDto dto) {
		log.debug("update({})", dto);
		
		// 업데이트 수행
		int result = userRepo.update(dto.getUserId(), dto.getUserPassword(), dto.getUserPhone());
		log.debug("update 결과 = {}", result);
	
	}
	
	//userId이용해서 예약정보찾기
	public List<ReservationMaster> readAllReservation(String userId){
		log.info("readAllReservation id={}",userId);
		return reservationMasterRepo.selectMasterByUserId(userId);
	}
	
    
    public Optional<ReservationMaster> readReservationList(Integer userKey) {
        Optional<ReservationMaster> list = reservationMasterRepo.findById(userKey);
        log.info("Reservation list for user {}: {}", userKey, list);
        return list;
    }

    public Optional<ReservationMaster> readReservationMasterDetails(Integer resId) {
        log.info("Finding reservation master details for resId: {}", resId);
        Optional<ReservationMaster> resMaster = reservationMasterRepo.findById(resId);
        log.info("Found ReservationMaster: {}", resMaster);
        return resMaster;
    }


    public List<ReservationDetailDto> readReservationDetails(Integer resId) {
        log.info("Finding reservation details for resvationMaster: {}", resId);
        List<ReservationDetailDto> resDetails = reservationDetailRepo.findDetailsByResId(resId);
        log.info("Found ReservationDetails: {}", resDetails);
        return resDetails;
    }
    
    
    public ReservationMasterDto getReservationMasterDto(Integer resId) {
        ReservationMaster master = reservationMasterRepo.findById(resId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        ReservationMasterDto dto = new ReservationMasterDto();
        dto.setResId(master.getResId());
        dto.setUserId(master.getUser().getUserId());
        dto.setRequirement(master.getRequirement());
        dto.setResCreatedTime(master.getResCreatedTime());
        dto.setResModifiedTime(master.getResModifiedTime());
        dto.setResCheckIn(master.getResCheckIn());
        dto.setResCheckOut(master.getResCheckOut());
        dto.setResTotalPrice(master.getResTotalPrice());
        dto.setResState(master.getResState());
        
        // ReservationDetail을 DTO로 변환
        dto.setReservationDetails(
            master.getReservationDetails().stream()
                .map(detail -> new ReservationDetailDto(
                    detail.getRdId(),
                    detail.getReservationMaster().getResId(),
                    detail.getItem().getItemId(),
                    detail.getItemQuantity(),
                    detail.getItemAmount(),
                    detail.getItem().getItemName(),
                    detail.getItem().getItemImg()
                ))
                .collect(Collectors.toList())
        );

        return dto;
    }
}