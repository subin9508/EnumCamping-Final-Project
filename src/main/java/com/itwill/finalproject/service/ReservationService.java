package com.itwill.finalproject.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.dto.ReservationDetailDto;
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
	
	// 전체 구역 리스트 가져오기
		public List<Items> getAllZones() {
			return itemsRepo.selectAllZones();
		}
	
	
	// 특정 아이템의 가격 조회
	public Integer readItemPrice(int itemId) {
		return itemsRepo.selectItemPrice(itemId);
	}
	

    @Transactional
    public void makeReservation(ReservationMaster reservationMaster, List<ReservationDetailDto> reservationDetails) {
        try {
            // ReservationMaster 엔티티를 먼저 저장하여 resId 생성
            log.debug("Attempting to save ReservationMaster: {}", reservationMaster);
            reservationMasterRepo.save(reservationMaster);
            log.debug("Successfully saved ReservationMaster with ID: {}", reservationMaster.getResId());

            // ReservationDetailDto 리스트를 ReservationDetail 엔티티로 변환하여 저장
            for (ReservationDetailDto detailDto : reservationDetails) {
                log.debug("Processing ReservationDetailDto: {}", detailDto);

                ReservationDetail reservationDetail = convertDtoToEntity(detailDto, reservationMaster);

                reservationDetailRepo.save(reservationDetail);
                log.debug("Successfully saved ReservationDetail with ID: {}", reservationDetail.getRdId());
            }
        } catch (Exception e) {
            log.error("Reservation failed", e);
            throw new RuntimeException("Reservation failed", e);
        }
    }

    private ReservationDetail convertDtoToEntity(ReservationDetailDto detailDto, ReservationMaster reservationMaster) {
        ReservationDetail reservationDetail = new ReservationDetail();
        reservationDetail.setReservationMaster(reservationMaster);

        Integer itemId = detailDto.getItemId();
        reservationDetail.setItemById(itemId, itemsRepo);

        if (detailDto.getItemQuantity() != null) {
            reservationDetail.setItemQuantity(detailDto.getItemQuantity());
        } else {
            reservationDetail.setItemQuantity(0); // 기본값 설정
        }

        reservationDetail.setItemAmount(detailDto.getItemAmount());

        return reservationDetail;
    }
	
	// userId에 해당하는 예약 상세 정보
    public List<ReservationDetailDto> getReservationDetailsByUserId(Integer userKey) {
        return reservationDetailRepo.selectDetailsByUserId(userKey);
    }
    
    // userId에 해당하는 예약 마스터 정보
    public ReservationMaster getReservationMasterByUserKey(Integer userKey) {
    	return reservationMasterRepo.selectMasterByUserKey(userKey);
    }
    
 // userId에 해당하는 예약 마스터 정보 삭제
    @Transactional
    public int deleteReservationMaster(Integer userKey) {
        log.info("Deleting reservationMaster for userId: {}", userKey);
        int result = reservationMasterRepo.deleteByUserId(userKey);
        log.info("Deleted {} reservationMaster records", result);
        return result;
    }
    
    // userId에 해당하는 예약 상세 정보 삭제
    @Transactional
    public int deleteReservationDetail(Integer userKey) {
    	log.info("reservatioDetail Delete");
    	int result = reservationDetailRepo.deleteByResId(userKey);
    	
    	return result;
    }

}
