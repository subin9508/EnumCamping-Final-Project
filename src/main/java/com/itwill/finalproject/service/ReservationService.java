package com.itwill.finalproject.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.repository.ItemsRepository;
import com.itwill.finalproject.repository.ReservationDetailRepository;
import com.itwill.finalproject.repository.ReservationMasterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {
	private final ReservationMasterRepository reservationMasterRepo;
	private final ItemsRepository itemsRepo;
	private final ReservationDetailRepository reservationDetailRepo;
	
	// 특정 날짜에 예약된 지역을 읽기
	public List<Integer> readReservedAreas(LocalDate date) {
	    LocalDate minusCheckIn = date.minusDays(1);
		
		return reservationMasterRepo.selectByResCheckIn(minusCheckIn);
	}
	
	// 특정 날짜와 지역에 해당하는 예약 마스터 정보를 읽어오기
	public List<ReservationMaster> readReservationMaster(LocalDate date, int area) {
		LocalDate plusCheckIn = date.plusDays(1);
		
		return reservationMasterRepo.selectByItemIdAndResCheckIn(area, plusCheckIn);
	}
	
	// 판매 대여 물품 아이템 리스트 가져오기
	public List<Items> getAllItems() {
		return itemsRepo.selectAllItems();
	}
	
	// 특정 아이템의 가격 조회
	public Integer readItemPrice(int itemId) {
		return itemsRepo.selectItemPrice(itemId);
	}
	
	// 예약 생성
	@Transactional
    public void makeReservation(ReservationMaster reservationMaster, List<ReservationDetail> reservationDetails) {
        try {
            // reservation_master 테이블에 데이터 삽입
        	log.debug("Attempting to save ReservationMaster: {}", reservationMaster);
            reservationMasterRepo.save(reservationMaster);
            log.debug("Successfully saved ReservationMaster with ID: {}", reservationMaster.getResId());
            
            // 자동 생성된 res_id 가져오기 (reservationMaster 엔티티에 resId가 자동으로 설정됨)
            int resId = reservationMaster.getResId();
            log.debug("resId({})", resId);

            // reservation_detail 테이블에 여러 데이터 삽입
            for (ReservationDetail detail : reservationDetails) {
            	log.debug("Processing ReservationDetail: {}", detail);
                detail.setReservationMaster(reservationMaster); // 예약 마스터 설정
                if(detail.getItemQuantity() == null) {
                    detail.setItemQuantity(0); // 기본값 설정
                }
                reservationDetailRepo.save(detail); // 예약 디테일 저장
                log.debug("Successfully saved ReservationDetail with ID: {}", detail.getRdId());
            }
        } catch (Exception e) {
            log.error("Reservation failed", e);
            throw new RuntimeException("Reservation failed", e);
        }
    }
	
	// userId에 해당하는 예약 상세 정보
    public List<ReservationDetail> getReservationDetailsByUserId(String userId) {
        return reservationDetailRepo.selectDetailsByUserId(userId);
    }
    
    // userId에 해당하는 예약 마스터 정보
    public ReservationMaster getReservationMasterByUserId(String userId) {
    	return reservationMasterRepo.selectMasterByUserId(userId);
    }

}
