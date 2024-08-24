package com.itwill.finalproject.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.dto.ReservationMasterDto;
import com.itwill.finalproject.dto.PaymentsDto;
import com.itwill.finalproject.exception.ServiceException;
import com.itwill.finalproject.repository.PaymentsCancelRepository;
import com.itwill.finalproject.repository.PaymentsRepository;
import com.itwill.finalproject.repository.ReservationMasterRepository;
import com.itwill.finalproject.repository.UserRepository;
import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.domain.PaymentsCancel;
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
    private PaymentsCancelRepository paymentsCancelRepo;

    @Autowired
    private ReservationMasterRepository reservationMasterRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private ReservationService reservationService;
       
    private IamportClient iamportClient;
    
    public PaymentsService() {
        this.iamportClient = new IamportClient("3360178750462177", "xzEAGVLFM1F39ck4e1ntRa5506p0RUqQceCLHIkHhLV2Ej4LehiDyotZjjLqfhd117dRVOEux5fsNMgT");
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
            Payments payment = paymentsRepo.findByResId(resId)
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

	       Payments payment = paymentsRepo.findById(payId)
	               .orElseThrow(() -> new ServiceException("Payment not found for payId: " + payId));

	       log.debug("Retrieved payment: {}", payment);

	       if ("CANCEL".equalsIgnoreCase(payment.getPayStatus())) {
	           log.info("Payment already cancelled for payId: {}", payId);
	           return "Payment already cancelled";
	       }

	       try {
	           String impUid = payment.getImpUid();
	           log.debug("impUid={}", impUid);

	           CancelData cancelData = new CancelData(impUid, true);
	           IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);

	           log.debug("Iamport API response: {}", response);

	           if (response != null && response.getResponse() != null 
	               && "cancelled".equalsIgnoreCase(response.getResponse().getStatus())) {

	               // 결제 상태를 CANCEL로 업데이트
	               payment.setPayStatus("CANCEL");
	               paymentsRepo.save(payment);

	               // 취소 내역을 PaymentsCancel 엔티티로 저장
	               PaymentsCancel paymentsCancel = PaymentsCancel.builder()
	                       .payId(payId)
	                       .impUid(payment.getImpUid())
	                       .resId(payment.getResId())
	                       .canAmount(payment.getResTotalPrice())
	                       .canDate(LocalDateTime.now())
	                       .canRole("구매자")
	                       .build();

	               paymentsCancelRepo.save(paymentsCancel);
	               	
	               // 예약 상태를 취소로 업데이트
	               Integer resId = payment.getResId();
	               updateReservationState(resId, 2);  // 2는 취소 상태
	               
	               log.info("Payment cancellation successful for payId: {}", payId);
	               return "Payment cancellation successful";
	           } else {
	        	// 실패 이유를 명확히 로그에 기록
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
	   
	   
	   @Transactional
	   public void updateReservationState(Integer resId, int resState) throws ServiceException {
	       try {
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
	    public String cancelPartialPayment(Integer payId, Integer cancelAmount) throws ServiceException {
	        log.debug("Attempting to partially cancel payment with payId: {} and cancelAmount: {}", payId, cancelAmount);

	        Payments payment = paymentsRepo.findById(payId)
	                .orElseThrow(() -> new ServiceException("Payment not found for payId: " + payId));

	        log.debug("Retrieved payment: {}", payment);
	        log.debug("impuid={}", payment.getImpUid());

	        if ("CANCEL".equalsIgnoreCase(payment.getPayStatus())) {
	            log.info("Payment already cancelled for payId: {}", payId);
	            return "Payment already cancelled";
	        }

	        try {
	            String impUid = payment.getImpUid();
	            log.debug("impUid={}", impUid);

	            // 부분 취소를 위해 취소 금액을 지정
	            CancelData cancelData = new CancelData(impUid, true, BigDecimal.valueOf(cancelAmount));
	            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);

	            log.debug("Iamport API response: {}", response);

	            // 응답에 대한 상세 로그 추가
	            if (response != null) {
	                log.debug("Iamport API response status: {}", response.getResponse().getStatus());
	                log.debug("Iamport API response message: {}", response.getResponse().getFailReason());
	            }

	            if (response != null && response.getResponse() != null
	                    && "cancelled".equalsIgnoreCase(response.getResponse().getStatus())) {

	                // 부분 취소가 성공하면, 결제 상태를 업데이트 (필요에 따라 금액 수정)
	                payment.setPayStatus("PARTIAL_CANCEL"); // 부분 취소 상태로 업데이트
	                payment.setResTotalPrice(payment.getResTotalPrice() - cancelAmount);
	                paymentsRepo.save(payment);

	                // 취소 내역을 PaymentsCancel 엔티티로 저장
	                PaymentsCancel paymentsCancel = PaymentsCancel.builder()
	                        .payId(payId)
	                        .impUid(payment.getImpUid())
	                        .resId(payment.getResId())
	                        .canAmount(cancelAmount)
	                        .canDate(LocalDateTime.now())
	                        .canRole("구매자")
	                        .build();

	                paymentsCancelRepo.save(paymentsCancel);

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
	            log.error("API call failed: ", e);
	            throw new ServiceException("API call failed: " + e.getMessage(), e);
	        } catch (Exception e) {
	            log.error("Error during partial cancellation", e);
	            throw new ServiceException("Error during partial cancellation: " + e.getMessage(), e);
	        }
	    }

		        		    
    
}