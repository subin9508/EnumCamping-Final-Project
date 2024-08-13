package com.itwill.finalproject.service;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.dto.ReservationMasterDto;
import com.itwill.finalproject.dto.PaymentsDto;
import com.itwill.finalproject.exception.ServiceException;
import com.itwill.finalproject.repository.PaymentsRepository;
import com.itwill.finalproject.repository.ReservationMasterRepository;
import com.itwill.finalproject.repository.UserRepository;
import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.domain.User;
import com.siot.IamportRestClient.IamportClient;
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
            Payments savedPayment = paymentsRepo.save(dto);
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
}