package com.itwill.finalproject.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.dto.AdditionalPaymentDto;
import com.itwill.finalproject.dto.ItemChangeDto;
import com.itwill.finalproject.dto.RefundRequestDto;
import com.itwill.finalproject.dto.ReservationChangeResultDto;
import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.dto.ReservationItemUpdateDto;
import com.itwill.finalproject.dto.ReservationUpdateDto;
import com.itwill.finalproject.exception.ServiceException;
import com.itwill.finalproject.repository.ItemsHistoryRepository;
import com.itwill.finalproject.repository.ItemsRepository;
import com.itwill.finalproject.repository.ReservationDetailRepository;
import com.itwill.finalproject.repository.ReservationMasterRepository;
import com.itwill.finalproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {
	private final ReservationMasterRepository reservationMasterRepo;
	private final ItemsRepository itemsRepo;
	private final ItemsHistoryRepository itemsHistoryRepo;
	private final ReservationDetailRepository reservationDetailRepo;
	private final UserRepository userRepo;

	//특가 예매 여부 체크
	public ReservationMaster findSpecial(String userId, LocalDateTime createdTime) {
		return reservationMasterRepo.selectSpecialPriceReservations(userId, createdTime);
	}
//	public ReservationMaster findSpecial(String userId, LocalDateTime createdTime) {
//	    return reservationMasterRepo.selectSpecialPriceReservations(userId, createdTime)
//	                                .orElseThrow(() -> new NoSuchElementException("특가 예매가 존재하지 않습니다."));
//	}
	
	
    // 가장 최근의 start_date를 조회하는 메소드
    public LocalDateTime getLatestStartDate(int itemId) {
    	//log.info("itemId = {}",itemId);
    	//log.info("date = {}",itemsHistoryRepo.findLatestStartDateByItemIdAndSpecial(itemId));
        return itemsHistoryRepo.findLatestStartDateByItemIdAndSpecial(itemId);
    }

    // 특정 조건에 따른 item_price를 조회하는 메소드
    public Integer getItemPriceByAdjustedEndDate(int itemId, LocalDateTime date) {
    	//log.info("id={}, date={}",itemId,date);
        return itemsHistoryRepo.findItemPriceByItemIdAndAdjustedEndDate(itemId, date);
    }
	
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
		if (itemsRepo.selectItemPrice(itemId) == null) {
			return -1; //item 테이블에서 special이 0인 경우
		}
		return itemsRepo.selectItemPrice(itemId);
	}
	
	
	public Integer readSpecialPrice(int itemId) {
		return itemsHistoryRepo.findSpecialPrice(itemId);
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
    
    // res_state 업데이트
    
    @Transactional
    public boolean updateReservationState(Integer resId, int newState) {
        log.debug("Updating reservation state for resId: {} to newState: {}", resId, newState);
        
        Optional<ReservationMaster> optionalReservation = reservationMasterRepo.findById(resId);
        
        if (optionalReservation.isPresent()) {
            ReservationMaster reservation = optionalReservation.get();
            reservation.setResState(newState);
            reservationMasterRepo.save(reservation);
            log.info("Successfully updated reservation state for resId: {}", resId);
            return true;
        } else {
            log.warn("Reservation not found for resId: {}", resId);
            return false;
        }
    }
    // updateReservation 메서드 수정
    @Transactional
    public ReservationChangeResultDto updateReservation(ReservationUpdateDto updateDto) {
        log.info("Updating reservation: {}", updateDto);
        
        ReservationMaster reservationMaster = reservationMasterRepo.findById(updateDto.getResId())
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

        ReservationChangeResultDto result = new ReservationChangeResultDto();
        result.setResId(reservationMaster.getResId());
        result.setOldCheckIn(reservationMaster.getResCheckIn());
        result.setOldCheckOut(reservationMaster.getResCheckOut());
        result.setNewCheckIn(updateDto.getNewCheckIn());
        result.setNewCheckOut(updateDto.getNewCheckOut());
        result.setOldTotalPrice(reservationMaster.getResTotalPrice());

        // 날짜 변경
        reservationMaster.setResCheckIn(updateDto.getNewCheckIn());
        reservationMaster.setResCheckOut(updateDto.getNewCheckOut());
        reservationMaster.setRequirement(updateDto.getNewRequirement());

        // 아이템 변경
        List<ItemChangeDto> itemChanges = updateReservationDetails(reservationMaster, updateDto.getUpdatedItems());
        result.setItemChanges(itemChanges);

        // 총 가격 재계산
        int newTotalPrice = calculateNewTotalPrice(reservationMaster, itemChanges);
        reservationMaster.setResTotalPrice(newTotalPrice);
        result.setNewTotalPrice(newTotalPrice);
        result.setPriceDifference(newTotalPrice - result.getOldTotalPrice());

        // 변경된 예약 정보 저장
        reservationMaster = reservationMasterRepo.save(reservationMaster);

        // 결과에 업데이트된 예약 정보와 사용자 정보 추가
        result.setUpdatedReservation(reservationMaster);
        result.setUser(reservationMaster.getUser());

        return result;
    }
    
	// 새로운 메서드 추가 (변경 결과 확인)
	public ReservationChangeResultDto getReservationChangeResult(Integer resId) {
		ReservationMaster reservation = reservationMasterRepo.findById(resId)
				.orElseThrow(() -> new RuntimeException("Reservation not found"));

		ReservationChangeResultDto result = new ReservationChangeResultDto();
		result.setResId(reservation.getResId());
		result.setOldCheckIn(reservation.getResCheckIn());
		result.setOldCheckOut(reservation.getResCheckOut());
		result.setOldTotalPrice(reservation.getResTotalPrice());
		result.setUpdatedReservation(reservation);
		result.setUser(reservation.getUser());

		// 여기에 변경된 예약 상세 정보를 설정하는 로직 추가
		List<ReservationDetailDto> updatedDetails = reservationDetailRepo.findDetailsByResId(resId);
		result.setUpdatedReservationDetails(updatedDetails);

		return result;
    }
    
    private List<ItemChangeDto> updateReservationDetails(ReservationMaster reservationMaster, List<ReservationItemUpdateDto> updatedItems) {
        return updatedItems.stream().map(updateItem -> {
            ReservationDetail detail = reservationDetailRepo.findByReservationMasterAndItem_ItemId(reservationMaster, updateItem.getItemId())
                .orElseThrow(() -> new RuntimeException("Reservation detail not found"));

            ItemChangeDto change = new ItemChangeDto();
            change.setItemId(detail.getItem().getItemId());
            change.setItemName(detail.getItem().getItemName());
            change.setOldQuantity(detail.getItemQuantity());
            change.setNewQuantity(updateItem.getNewQuantity());
            change.setOldPrice(detail.getItemAmount());

            detail.setItemQuantity(updateItem.getNewQuantity());
            detail.setItemAmount(detail.getItem().getItemPrice() * updateItem.getNewQuantity());
            
            change.setNewPrice(detail.getItemAmount());

            reservationDetailRepo.save(detail);
            return change;
        }).collect(Collectors.toList());
    }

    private int calculateNewTotalPrice(ReservationMaster reservationMaster, List<ItemChangeDto> itemChanges) {
        int totalPrice = 0;
        for (ItemChangeDto change : itemChanges) {
            totalPrice += change.getNewPrice();
        }
        return totalPrice;
    }

    @Transactional
    public boolean processAdditionalPayment(AdditionalPaymentDto paymentDto) {
        log.info("Processing additional payment: {}", paymentDto);
        
        ReservationMaster reservation = reservationMasterRepo.findById(paymentDto.getResId())
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // 여기에 실제 결제 처리 로직 구현 (예: PG사 API 호출)
        
        // 결제 성공 시 예약 상태 업데이트
        reservation.setResTotalPrice(reservation.getResTotalPrice() + paymentDto.getAmount());
        reservationMasterRepo.save(reservation);
        
        return true; // 결제 성공 시 true 반환
    }

    @Transactional
    public boolean processRefundRequest(RefundRequestDto refundDto) {
        log.info("Processing refund request: {}", refundDto);
        
        ReservationMaster reservation = reservationMasterRepo.findById(refundDto.getResId())
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // 여기에 실제 환불 처리 로직 구현 (예: PG사 API 호출)
        
        // 환불 성공 시 예약 상태 업데이트
        reservation.setResTotalPrice(reservation.getResTotalPrice() - refundDto.getAmount());
        reservation.setResState(2); // 환불 상태로 변경 (상태 코드는 실제 시스템에 맞게 조정 필요)
        reservationMasterRepo.save(reservation);
        
        return true; // 환불 요청 성공 시 true 반환
    }

	// 새로운 메서드 추가
	@Transactional
	public ReservationChangeResultDto finalizeReservationUpdate(ReservationUpdateDto updateDto) {
		log.info("Finalizing reservation update: {}", updateDto);

		ReservationChangeResultDto changeResult = updateReservation(updateDto);

		// 결제 처리 (추가 결제 또는 환불)
		processPaymentChange(changeResult);

		return changeResult;
	}

	// 새로운 메서드 추가
	private void processPaymentChange(ReservationChangeResultDto changeResult) {
		if (changeResult.getPriceDifference() > 0) {
			// 추가 결제 처리
			processAdditionalPayment(
					new AdditionalPaymentDto(changeResult.getResId(), changeResult.getPriceDifference()));
		} else if (changeResult.getPriceDifference() < 0) {
			// 환불 처리
			processRefundRequest(
					new RefundRequestDto(changeResult.getResId(), Math.abs(changeResult.getPriceDifference())));
		}
	}
	

    @Transactional // 트랜잭션을 관리합니다. 메서드가 DB 변경을 포함하기 때문에 필요합니다.
    public void updateResModifiedTime(Integer resId) {
        // Repository에서 JPQL을 실행하는 메서드를 호출합니다.
        reservationMasterRepo.updateResModifiedTime(resId);
    }
    
    // 예약 ID로 체크인 날짜를 가져오는 메서드
    public LocalDate getCheckinDateByResId(Integer resId) throws ServiceException {
        return reservationMasterRepo.findById(resId)
                .map(ReservationMaster::getResCheckIn) // ReservationMaster에서 체크인 날짜 가져오기
                .orElseThrow(() -> new ServiceException("예약 정보를 찾을 수 없습니다. resId: " + resId));
    }
    
    
//    // 체크인, 체크아웃 기간에 예약된 구역을 조회
//    public List<Integer> readReservedAreasBetween(LocalDate checkIn, LocalDate checkOut) {
//        // 해당 기간에 예약된 구역 목록을 가져옴
//        return reservationMasterRepo.findReservedAreasBetweenDates(checkIn, checkOut);
//    }

}
