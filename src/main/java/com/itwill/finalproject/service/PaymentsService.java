package com.itwill.finalproject.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.dto.ReservationMasterDto;
import com.itwill.finalproject.dto.PaymentsDto;
import com.itwill.finalproject.exception.ServiceException;
import com.itwill.finalproject.repository.ClaimMasterRepository;
import com.itwill.finalproject.repository.PaymentsRepository;
import com.itwill.finalproject.repository.ReservationMasterRepository;
import com.itwill.finalproject.repository.SpecialRepository;
import com.itwill.finalproject.repository.UserRepository;
import com.itwill.finalproject.domain.ClaimMaster;
import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class PaymentsService {
    
    @Autowired
    private PaymentsRepository paymentsRepo;
    
    @Autowired
    private ReservationMasterRepository reservationMasterRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private SpecialRepository specialRepo;
    
    @Autowired
    private ReservationService reservationService;
    
    private ReservationMaster reservationMaster;
    
    @Autowired
    private ClaimMasterRepository clmMasterRepo;
    
    private IamportClient iamportClient;
    
    public PaymentsService() {
        this.iamportClient = new IamportClient("******************", "*********************************************************************");
    }

    public Map<String, Object> getPaymentInfoByResId(Integer resId) throws ServiceException {
        try {
            ReservationMasterDto reservation = reservationMasterRepo.findById(resId)
                .map(ReservationMasterDto::of)
                .orElseThrow(() -> new ServiceException("Reservation not found for resId: " + resId));

            User user = userRepo.findByUserId(reservation.getUserId())
                .orElseThrow(() -> new ServiceException("User not found for userId: " + reservation.getUserId()));
            
            Map<String, Object> paymentInfo = new HashMap<>(); 
            paymentInfo.put("name", "예약 번호 " + reservation.getResId());
            paymentInfo.put("amount", reservation.getResTotalPrice());
            paymentInfo.put("email", user.getUserEmail());
            paymentInfo.put("buyerName", user.getUsername()); //이거 아마 유저아이디로 나올거임
            paymentInfo.put("phoneNumber", user.getUserPhone());
            paymentInfo.put("resState", reservation.getResState());

            return paymentInfo;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    public String savePayment(Payment payment, Integer resId) throws ServiceException {
        log.trace("savePayment({}, {}) invoked.", payment, resId);

        PaymentsDto dto = new PaymentsDto(); 
        dto.setImpUid(payment.getImpUid());
        dto.setPgTid(payment.getPgTid());
        dto.setResId(resId);
        dto.setResTotalPrice(payment.getAmount().intValue());
        
        if (payment.getPaidAt() != null) {
            dto.setPayDate(payment.getPaidAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        
        dto.setPayMethod(payment.getPayMethod());
        dto.setPayStatus(payment.getStatus());
        dto.setBuyerEmail(payment.getBuyerEmail());

        try {
            Payments savedPayment = paymentsRepo.save(dto.toEntity());
            return savedPayment != null ? "SUCCESS" : "FAIL:01";
        } catch (Exception e) {
            log.error("Error saving payment", e);
            throw new ServiceException("Failed to save payment", e);
        }
    }
    
    @Transactional
    public void updateReservationState(Integer resId, Integer resState) throws ServiceException {
        try {
        	paymentsRepo.updateReservationState(resId, resState);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * resId를 사용하여 결제 정보를 조회하는 메서드
     * @param resId 예약 ID
     * @return Payments 객체를 반환하거나, 존재하지 않으면 예외를 던짐
     * @throws ServiceException 결제 정보가 존재하지 않을 경우 예외 발생
     */
    public Payments getPaymentByResId(Integer resId) throws ServiceException {
        Optional<Payments> payment = paymentsRepo.findByResId(resId);
        return payment.orElseThrow(() -> new ServiceException("결제 정보를 찾을 수 없습니다."));
    }
    
    // --------------------------- 결제 취소 부분
    /**
	 * 예약 ID로 결제 ID를 조회하는 메서드
	 * 
	 * @param resId 예약 ID
	 * @return 결제 ID
	 * @throws ServiceException 예외 발생 시 ServiceException으로 wrapping 하여 throw
	 */
    @Transactional(readOnly = true)
    public Integer getPayIdByResId(Integer resId) throws ServiceException {
        try {

            Payments payment = paymentsRepo.findTopByResIdAndPayStatusOrderByPayIdDesc(resId, "paid")

                .orElseThrow(() -> new ServiceException("Payment not found for resId: " + resId));
            return payment.getPayId();
        } catch (Exception e) {
            log.error("Failed to retrieve payment ID for reservation ID: {}", resId, e);
            throw new ServiceException(e);
        }
    }
	
	
	/**
	 * 결제 ID로 예약 ID를 조회하는 메서드
	 * 
	 * @param payId 결제 ID
	 * @return 예약 ID
	 * @throws ServiceException 예외 발생 시 ServiceException으로 wrapping 하여 throw
	 */
    @Transactional(readOnly = true)
    public Integer getResIdByPayId(Integer payId) throws ServiceException {
        try {
            Payments payment = paymentsRepo.findById(payId)
                    .orElseThrow(() -> new ServiceException("Payment not found for payId: " + payId));
            return payment.getResId();
        } catch (Exception e) {
            log.error("Failed to retrieve reservation ID for payment ID: {}", payId, e);
            throw new ServiceException("Failed to retrieve reservation ID for payment ID: " + payId, e);
        }
    }
	
	// 결제 취소 메서드 
	   @Transactional
	   public String cancelPayment(Integer payId) throws ServiceException {
	       log.debug("Attempting to cancel payment with payId: {}", payId);
	       
	       // 결제 정보 조회
	       Payments payment = paymentsRepo.findById(payId)
	               .orElseThrow(() -> new ServiceException("Payment not found for payId: " + payId));

	       log.debug("Retrieved payment: {}", payment);
	       
	       // 결제 상태가 이미 취소된 경우
	       if ("cancel".equalsIgnoreCase(payment.getPayStatus())) {
	           log.info("Payment already cancelled for payId: {}", payId);
	           return "Payment already cancelled";
	       }
	       
	       // 현재 날짜와 체크인 날짜 계산
	       LocalDate currentDate = LocalDate.now();
	       LocalDate checkinDate = reservationService.getCheckinDateByResId(payment.getResId());
	       long daysBeforeCheckin = ChronoUnit.DAYS.between(currentDate, checkinDate);
	       
	       
	       // 예약 정보 조회
	       ReservationMaster reservation = reservationMasterRepo.findById(payment.getResId())
	               .orElseThrow(() -> new ServiceException("Reservation not found for resId: " + payment.getResId()));

	       // 특가 예약 여부 확인
//	       if (reservation.getResSpecial() == 1) {
//	           // ReservationDetail에서 첫 번째 아이템의 itemId를 가져옴
//	           Integer itemId = reservation.getReservationDetails().get(0).getItem().getItemId();
//
//	           // 특가 종료일 확인
//	           LocalDateTime specialEndDateTime = specialRepo.findEndDateByItemId(itemId)
//	                   .orElseThrow(() -> new ServiceException("Special end date not found for itemId: " + itemId));
//
//	           // LocalDateTime을 LocalDate로 변환
//	           LocalDate specialEndDate = specialEndDateTime.toLocalDate();
//
//	           // 특가 종료일이 현재 날짜 이전인 경우 (특가 기간 이후)
//	           if (specialEndDate.isBefore(currentDate)) {
//	               log.info("특가 기간 이후 취소: 100% 수수료 부과");
//	               return "환불이 불가능한 상태입니다. (특가 기간 이후 취소)";
//	           }
//	       }
	       
	       
	       // 환불 비율 결정
	       double refundRate = 0.0;
	       if (daysBeforeCheckin >= 7) {
	           refundRate = 1.0; // 100% 환불
	       } else if (daysBeforeCheckin >= 4 && daysBeforeCheckin <= 6) {
	           refundRate = 0.5; // 50% 환불
	       } else {
	           refundRate = 0.0; // 환불 불가
	       }
	       
	       ClaimMaster clmMaster = clmMasterRepo.findByPayIdMaxClmId(payId);
	       BigDecimal refundAmount = null;
	       
	       if (clmMaster == null) {
	       // 환불 비율에 따른 환불 금액 계산
	    	   refundAmount = BigDecimal.valueOf(payment.getResTotalPrice() * refundRate);
	       } else if (clmMaster != null) {
	    	   refundAmount = BigDecimal.valueOf(clmMaster.getTotalPrice() * refundRate);
	       }
	       if (refundRate == 0.0) {
	           log.info("환불 불가: payId: {}", payId);
	           return "환불이 불가능한 상태입니다. (체크인 날짜 임박)";
	       }
	       
	       
	       try {
	    	   // 결제 취소 요청 
	           String impUid = payment.getImpUid();
	           log.debug("impUid={}", impUid);

	           CancelData cancelData = new CancelData(impUid, true, refundAmount);
	           IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);

	           log.debug("Iamport API response: {}", response);

	           if (response != null && response.getResponse() != null 
	               && "cancelled".equalsIgnoreCase(response.getResponse().getStatus())) {

	               // 새로운 결제 정보 생성 및 저장
	               Payments newPayment = new Payments();
	               newPayment.setResId(payment.getResId());
	               newPayment.setImpUid(impUid);
	               newPayment.setPgTid(payment.getPgTid());;
	               newPayment.setPayStatus("cancel");
	               newPayment.setResTotalPrice(refundAmount.intValue());  // 취소된 금액
	               newPayment.setPayMethod(payment.getPayMethod());
	               newPayment.setBuyerEmail(payment.getBuyerEmail());
	               newPayment.setPayDate(LocalDateTime.now());
	               paymentsRepo.save(newPayment);             
	               	
	               // 포인트 결제인 경우에도, 예약 상태를 '예약 변경 완료'로 설정
	               if ("point".equalsIgnoreCase(payment.getPayMethod())) {
	                   updateReservationState(payment.getResId(), 3); // 3: 예약 변경 완료
	               } else {
	                   updateReservationState(payment.getResId(), 2); // 2: 예약 취소
	               }

	               log.info("Payment cancellation successful for payId: {}", payId);
	               return "Payment cancellation successful";
	            } else {
	               if (response != null && response.getResponse() != null) {
	                   log.error("Cancellation failed: Status = {}, Message = {}",
	                       response.getResponse().getStatus(), response.getResponse().getFailReason());
	               } else {
	                   log.error("Cancellation failed: No response from PG site.");
	               }
	               throw new ServiceException("Cancellation failed: Payment status is not cancelled on PG site");
	           }
	       } catch (IamportResponseException e) {
	           log.error("API call failed: ", e);
	           throw new ServiceException("API call failed: " + e.getMessage(), e);
	       } catch (Exception e) {
	           log.error("Error during cancellation", e);
	           throw new ServiceException("Error during cancellation: " + e.getMessage(), e);
	       }
	    }
	   
	   // 최신 결제 정보를 가져오는 메서드 추가
	    public Payments getLatestPaymentByResId(Integer resId) throws ServiceException {
	    	 List<Payments> payments = paymentsRepo.findByResIdOrderByPayDateDesc(resId); // 최신 결제 내역을 가져오는 쿼리
	    	    if (payments.isEmpty()) {
	    	        throw new ServiceException("No payments found for resId: " + resId);
	    	    }
	    	    return payments.get(0);  // 최신 결제 내역 반환 (가장 첫 번째 값)
	    }
	   
	   
	   @Transactional
	   public void updateReservationState(Integer resId, int resState) throws ServiceException {
		   try {
		        // 동일한 resId에 대해 가장 최신 결제 내역을 가져옴
		        Payments latestPayment = getLatestPaymentByResId(resId);

		        // 결제 수단이 'point'이고 추가 결제가 있었던 경우 예약 상태를 '예약 변경 완료'로 업데이트
		        if ("point".equalsIgnoreCase(latestPayment.getPayMethod()) && "cancel".equalsIgnoreCase(latestPayment.getPayStatus())) {
		            resState = 3;  // 예약 변경 완료 상태로 설정
		        }

		        ReservationMaster reservation = reservationMasterRepo.findById(resId)
		                .orElseThrow(() -> new ServiceException("Reservation not found for resId: " + resId));
		        reservation.setResState(resState);
		        reservationMasterRepo.save(reservation);
		    } catch (Exception e) {
	           log.error("Failed to update reservation state for resId: {}", resId, e);
	           throw new ServiceException("Failed to update reservation state: " + e.getMessage(), e);
	       }
	   }
	   
	   
	   // 부분 취소 메서드
	    @Transactional
	    public String cancelPartialPayment(Integer payId, Integer cancelAmount, LocalDate checkinDate) throws ServiceException {
	        log.debug("Attempting to partially cancel payment with payId: {} and cancelAmount: {}", payId, cancelAmount);

	        Payments payment = paymentsRepo.findById(payId)
	                .orElseThrow(() -> new ServiceException("Payment not found for payId: " + payId));
	        
	        // 현재 날짜를 가져옴
	        LocalDate currentDate = LocalDate.now();
	        
	        // 남은 날짜 계산
	        long daysBeforeCheckin = ChronoUnit.DAYS.between(currentDate, checkinDate);
	        double refundRate = 0;
	        
	        // 환불 조건 적용
	        if (daysBeforeCheckin >= 7) {
	            refundRate = 1.0; // 100% 환불
	        } else if (daysBeforeCheckin >= 4 && daysBeforeCheckin <= 6) {
	            refundRate = 0.5; // 50% 환불
	        } 
	        if (refundRate == 0.0) {
	            log.info("환불 불가: payId: {}", payId);
	            return "환불이 불가능한 상태입니다. (당일 취소)";
	        }
	        
	        
	        
	        log.debug("Retrieved payment: {}", payment);
	        log.debug("impuid={}", payment.getImpUid());
	        
//	     // Payment method가 point인 경우 결제창을 띄워야 함
//	        if ("point".equalsIgnoreCase(payment.getPayMethod())) {
//	            log.info("Payment method is point for payId: {}, requiring user interaction", payId);
//	            return "Payment method requires confirmation on UI";
//	        }
	        
	        log.debug("Retrieved payment with resId: {}", payment.getResId());
	        // Ensure the resId here is what you expect
	        Integer resId = payment.getResId();
	        log.debug("Related resId: {}", resId);
	        
			if ("CANCEL".equalsIgnoreCase(payment.getPayStatus())) {
				log.info("Payment already cancelled for payId: {}", payId);
				return "Payment already cancelled";
			}

	        try {
	            String impUid = payment.getImpUid();
	            BigDecimal partialCancelAmount = BigDecimal.valueOf(cancelAmount * refundRate); // 환불 비율을 반영한 금액 계산
	            log.debug("impUid={}", impUid);

	            // 부분 취소를 위해 취소 금액을 지정 (부분 취소 데이터 생성)
	            CancelData cancelData = new CancelData(impUid, true, partialCancelAmount);
	            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);

	            log.debug("Iamport API response: {}", response);

	            // 응답에 대한 상세 로그 추가
	            if (response != null) {
	                log.debug("Iamport API response status: {}", response.getResponse().getStatus());
	                log.debug("Iamport API response message: {}", response.getResponse().getFailReason());
	            }

	            if (response != null && response.getResponse() != null
	                    && "cancelled".equalsIgnoreCase(response.getResponse().getStatus())) {

	                // 부분 취소가 성공하면, 결제 상태를 인서트 (필요에 따라 금액 수정)
	            	 // 새로운 결제 정보 생성 및 저장
		               Payments newPayment = new Payments();
		               newPayment.setResId(payment.getResId());
		               newPayment.setImpUid(impUid);
		               newPayment.setPgTid(payment.getPgTid());;
		               newPayment.setPayStatus("PARTIAL_CANCEL");
		               newPayment.setResTotalPrice(cancelAmount);  // 취소된 금액
		               newPayment.setPayMethod(payment.getPayMethod());
		               newPayment.setBuyerEmail(payment.getBuyerEmail());
		               newPayment.setPayDate(LocalDateTime.now());
		               paymentsRepo.save(newPayment);

	                // 예약 상태 업데이트
	                boolean updateSuccess = reservationService.updateReservationState(payment.getResId(), 3);
	                if (!updateSuccess) {
	                    log.warn("Failed to update reservation state for resId: {}", payment.getResId());
	                }

	                log.info("Partial payment cancellation successful for payId: {}", payId);
	                return "Partial payment cancellation successful";
	            } else {
	                if (response != null && response.getResponse() != null) {
	                    log.error("Partial cancellation failed: Status = {}, Message = {}",
	                            response.getResponse().getStatus(), response.getResponse().getFailReason());
	                } else {
	                    log.error("Partial cancellation failed: No response from PG site.");
	                }
	                throw new ServiceException("Partial cancellation failed: Payment status is not cancelled on PG site");
	            }
	        } catch (IamportResponseException e) {
	            log.error("API 호출 실패: ", e);
	            throw new ServiceException("API 호출 실패: " + e.getMessage(), e);
	        } catch (Exception e) {
	            log.error("Error during partial cancellation", e);
	            throw new ServiceException("Error during partial cancellation: " + e.getMessage(), e);
	        }
	    }
	  	  
	    // 추가결제 메서드
	    public String saveAdditionalPayment(Payment payment, Integer resId) throws ServiceException {

//	        // 새로운 결제 기록을 생성
	    	PaymentsDto dto = new PaymentsDto();
	    	dto.setImpUid(payment.getImpUid());
	    	dto.setPgTid(payment.getPgTid());
	    	dto.setResId(resId);
	    	dto.setResTotalPrice(payment.getAmount().intValue());
	    	
	    	if (payment.getPaidAt() != null) {
	            dto.setPayDate(payment.getPaidAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	        }
	        
	        dto.setPayMethod(payment.getPayMethod());
	        dto.setPayStatus(payment.getStatus());
	        dto.setBuyerEmail(payment.getBuyerEmail());
//
//	        // 예약 정보를 업데이트
	        try {
	            Payments savedPayment = paymentsRepo.save(dto.toEntity());
	            return savedPayment != null ? "SUCCESS" : "FAIL:01";
	        } catch (Exception e) {
	            log.error("Error saving payment", e);
	            throw new ServiceException("Failed to save payment", e);
	        }
	    }

    
}