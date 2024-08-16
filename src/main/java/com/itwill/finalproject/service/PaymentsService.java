package com.itwill.finalproject.service;

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
	                       .build();

	               paymentsCancelRepo.save(paymentsCancel);

	               log.info("Payment cancellation successful for payId: {}", payId);
	               return "Payment cancellation successful";
	           } else {
	               log.error("Cancellation failed: Payment status is not cancelled on PG site");
	               return "Cancellation failed: Payment status is not cancelled on PG site";
	           }
	       } catch (IamportResponseException e) {
	           log.error("API call failed: ", e);
	           throw new ServiceException("API call failed: " + e.getMessage(), e);
	       } catch (Exception e) {
	           log.error("Error during cancellation", e);
	           throw new ServiceException("Error during cancellation: " + e.getMessage(), e);
	       }
	   }
		        		    
    
}