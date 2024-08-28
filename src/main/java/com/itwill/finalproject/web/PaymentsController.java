package com.itwill.finalproject.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.dto.ReservationListDto;
import com.itwill.finalproject.exception.ControllerException;
import com.itwill.finalproject.exception.ServiceException;
import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.service.MyPageService;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.service.PaymentsService;
import com.itwill.finalproject.service.ReservationService;
import com.itwill.finalproject.service.UserService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/") // 해당 클래스의 기본 URL 매핑을 설정
public class PaymentsController {

    // 아임포트 API와 상호작용하기 위한 클라이언트 객체를 정의
    private IamportClient api;

    // 스프링의 @Autowired를 사용하여 의존성을 주입받을 필드를 정의
    private final PaymentsService paymentsService;
    private final UserService userService;
    private final MyPageService mypageService;
    private final ReservationService reservationService;

    // 생성자를 통해 의존성 주입
    public PaymentsController(PaymentsService paymentsService, UserService userService, MyPageService myPageService, ReservationService reservationService) {
        this.paymentsService = paymentsService;
        this.userService = userService;
        this.mypageService = myPageService;
        this.reservationService = reservationService;

        // 가맹점 식별키와 비밀키 전달하여 api 인증
        this.api = new IamportClient("3360178750462177",
            "xzEAGVLFM1F39ck4e1ntRa5506p0RUqQceCLHIkHhLV2Ej4LehiDyotZjjLqfhd117dRVOEux5fsNMgT");
    }

    // resId 파라미터 받아서 결제 서비스를 통해 해당하는 결제 정보 조회하고 JSON 형식으로 반환. 예외처리 통해 내부 오류 처리하고
    // 응답 반환.
    @GetMapping("/reservation/paymentInfo/{resId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPaymentInfo(@PathVariable("resId") Integer resId) {
        try {
            Map<String, Object> paymentInfo = paymentsService.getPaymentInfoByResId(resId); // 결제 정보를 조회
            return ResponseEntity.ok(paymentInfo); // 조회된 결제 정보를 반환
        } catch (ServiceException e) {
            // 예외 발생 시 에러 로그를 출력
            log.error("Error fetching payment info for resId: {}", resId, e);
            // 서버 내부 오류가 발생한 경우, HTTP 상태 코드 500과 함께 null 값을 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @ResponseBody
    @PostMapping("/reservation/verifyIamport/{imp_uid}")
    public ResponseEntity<?> paymentByImpUid(
            @PathVariable(value = "imp_uid") String imp_uid,
            @RequestParam("resId") Integer resId
    ) throws IamportResponseException, IOException, ControllerException {
        log.trace("paymentByImpUid({}, {}) invoked.", imp_uid, resId);

        try {
            // 먼저 예약 상태를 확인
            Map<String, Object> paymentInfo = this.paymentsService.getPaymentInfoByResId(resId);
            log.debug("---------paymentInfoByResId = {}", paymentInfo);
            if(paymentInfo != null) {
                Object resStateObj = paymentInfo.get("resState");
                log.debug("---------resStateObj = {}", resStateObj);
                if(resStateObj != null) {
                    int resState = ((Number) resStateObj).intValue();
                    log.debug("-----------resState = {}", resState);
                    if(resState == 1) {
                        // 이미 결제가 완료된 예약이면 오류 반환
                        return ResponseEntity.badRequest().body("이미 결제가 완료된 예약입니다.");
                    }
                }
            }

            // 아임포트 API를 통해 결제 정보를 조회
            Payment payment = this.api.paymentByImpUid(imp_uid).getResponse();
            
            if ("paid".equals(payment.getStatus())) {
                String result = this.paymentsService.savePayment(payment, resId);
                log.info("Payment saved successfully: {}", result);
                
                // 결제 성공 시 예약 상태를 1로 업데이트
                this.paymentsService.updateReservationState(resId, 1);
          
                // 결제 정보를 그대로 반환
                return ResponseEntity.ok(Map.of(
                        "status", payment.getStatus(),
                        "merchant_uid", payment.getMerchantUid(),
                        "payment", payment
                    ));
            } else if ("failed".equals(payment.getStatus())) { // 결제가 실패한 경우
                return ResponseEntity.badRequest().body("결제 실패: " + payment.getFailReason());
            } else { // 알 수 없는 결제 상태인 경우
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 결제 상태");
            }
        } catch (IamportResponseException | IOException e) { // 예외 처리
            log.error("결제 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 검증 실패: " + e.getMessage());
        } catch (ServiceException e) {
            log.error("결제 정보 저장 중 오류 발생", e);
            throw new ControllerException(e); // 예외를 다시 던져서 처리
        }
    }
    
    @GetMapping("/reservation/successed/{resId}")
    public String paymentSucceessed(@PathVariable("resId") Integer resId , Model model, HttpSession session) {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String userId = authentication.getName();
    	
//    	Integer rdId = (Integer) session.getAttribute("rdId"); // 세션에서 rdId 가져오기
        Optional<ReservationMaster> resMaster = mypageService.readReservationMasterDetails(resId);

        List<ReservationDetailDto> resDetail = mypageService.readReservationDetails(resId);

        
//        String userId = (String) session.getAttribute("userId"); // 세션에서 userId 가져오기
        model.addAttribute("res_id", resId); // 모델에 resId 추가
        model.addAttribute("resMaster", resMaster);
        model.addAttribute("resDetail", resDetail);
        model.addAttribute("userId", userId); // 모델에 userId 추가

        return "reservation/successed"; // succeeded.html 파일을 가리킴
    }
    
    
    // ------------------------ 결제 취소 -----------------------
    
	/**
	 * resId를 통해 payId를 조회하는 메서드(결제취소시 사용)
	 * @param resId
	 * @return 에러 메세지 반환
	 */
    @GetMapping("/mypage/reservation_details/getPayId/{resId}")
    public ResponseEntity<?> getPayId(@PathVariable("resId") Integer resId) {
        try {
            Integer payId = paymentsService.getPayIdByResId(resId); // 결제 ID를 조회
            return ResponseEntity.ok(payId); // 조회된 결제 ID를 반환
        } catch (ServiceException e) {
            log.error("Error retrieving payment ID for resId: {}", resId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // 예외 발생 시 에러 메시지를 반환
        }
    }
	
	
	/**
	 * 결제 취소 요청을 처리하는 메서드
	 * @param payId 결제 키로 결제를 식별
	 * @return ResponseEntity 객체로 HTTP 응답 상태와 메세지를 반환.
	 */
    @ResponseBody
    @PostMapping("/mypage/reservation_details/cancel/{payId}")
    public ResponseEntity<String> cancelPayment(@PathVariable("payId") Integer payId) {
    	
        if (payId == null || payId <= 0) {
            return ResponseEntity.badRequest().body("Invalid payId");
        }
    	
        try {
            String result = paymentsService.cancelPayment(payId);
            if ("Payment cancellation successful".equals(result)) {
                // 결제 취소가 성공했을 때 예약 상태를 업데이트
                Integer resId = paymentsService.getResIdByPayId(payId);
                if (resId != null) {
                    paymentsService.updateReservationState(resId, 2); // 2는 취소 상태
                    log.info("Reservation state updated to cancelled for resId: {}", resId);
                    return ResponseEntity.ok(result);
                } else {
                    log.warn("Could not find reservation for payId: {}", payId);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reservation not found for the given payment ID.");
                }
            } else {
            	log.warn("Cancellation failed: {}", result);
                // 결과 메시지에 따라 적절한 HTTP 상태 코드를 반환
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (ServiceException e) {
            log.error("Error during payment cancellation for payId: {}", payId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cancellation failed: " + e.getMessage());
        }
    }
    
    //------------------- 부분 취소 ----------------------
    /**
     * resId를 통해 결제 정보를 조회하는 메서드 (결제 취소 시 사용)
     * @param resId
     * @return 결제 정보 객체를 반환하거나 에러 메시지 반환
     */
    @GetMapping("/mypage/reservation_update/getPaymentInfo/{resId}")
    public ResponseEntity<?> getPartialPaymentInfo(@PathVariable("resId") Integer resId) {
        try {
        	Payments payment = paymentsService.getPaymentByResId(resId); // 결제 정보를 조회

            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("결제 정보를 찾을 수 없습니다.");
            }

            // 필요한 결제 정보를 Map에 담아 반환
            Map<String, Object> paymentInfo = new HashMap<>();
            paymentInfo.put("payId", payment.getPayId());
            paymentInfo.put("payMethod", payment.getPayMethod());
            log.debug("payId={}, payMethod={}", payment.getPayId(), payment.getPayMethod());

            return ResponseEntity.ok(paymentInfo); // 조회된 결제 정보를 반환
        } catch (ServiceException e) {
            log.error("Error retrieving payment information for resId: {}", resId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // 예외 발생 시 에러 메시지를 반환
        }
    }

    
    
    
    @ResponseBody
    @GetMapping("/mypage/reservation_update/refund/{payId}")
    @PostMapping("/mypage/reservation_update/refund/{payId}")
    public ResponseEntity<String> cancelPartialPayment(
            @PathVariable("payId") Integer payId,
            @RequestParam("cancelAmount") Integer cancelAmount
            ) {
    	
    	log.info("payId: {}, cancelAmount: {}", payId, cancelAmount);  // payId와 cancelAmount 로그 확인

    	
        if (payId == null || payId <= 0 || cancelAmount == null || cancelAmount <= 0) {
            return ResponseEntity.badRequest().body("Invalid payId or cancelAmount");
        }
        
        
        try {
            String result = paymentsService.cancelPartialPayment(payId, cancelAmount);
            if ("Partial payment cancellation successful".equals(result)) {
                // 부분 취소가 성공했을 때 예약 상태를 업데이트
                boolean updateSuccess = reservationService.updateReservationState(payId, 3);
                if (updateSuccess) {
                    log.info("Partial payment cancelled and reservation state updated successfully for payId: {}", payId);
                    return ResponseEntity.ok("Partial payment cancellation and reservation update successful");
                } else {
                    log.warn("Partial payment cancelled but reservation state update failed for payId: {}", payId);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Partial cancellation successful but reservation update failed");
                }
            } else {
                log.warn("Partial cancellation failed: {}", result);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (ServiceException e) {
            log.error("Error during partial payment cancellation for payId: {}", payId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Partial cancellation failed: " + e.getMessage());
        }
    }


    
    //------------------- 내 예약 목록 조회 ----------------------
    /**
     * 예약 목록을 조회하는 메서드
     *
     * @param userId 사용자 ID
     * @param model  모델 객체
     * @param session 세션 객체
     * @return 뷰 이름 반환
     */
//    @GetMapping("/reservation_list")
//    public String reservationList(@RequestParam(name = "userId", required = false) String userId, Model model, HttpSession session) {
//        if (userId == null) {
//            userId = (String) session.getAttribute("signedInUser");
//            if (userId == null) {
//                return "redirect:/user/signin";
//            }
//        }
//
//        log.debug("reservation_list(userId={})", userId);
//
//        User user = userService.read(userId);
//        session.setAttribute("user", user); // 사용자 정보를 세션에 저장
//
//        List<ReservationListDto> list = userService.readReservationList(user.getUserId());
//        log.debug("list=({})", list);
//        model.addAttribute("reservations", list);
//        model.addAttribute("user", user); // 모델에 사용자 정보 추가
//
//        return "/user/reservation_list"; // 반환할 뷰의 이름
//    }
	
	// pg 사에서 결제 취소했을 경우, 웹훅 사용
//	@PostMapping("/webhooks/payment/cancellation")
//	public ResponseEntity<String> handlePaymentCancellation(@RequestBody Map<String, Object> payload) {
//	    // 페이로드 검증 로직 (필요한 경우)
//	    // 결제 취소 로직 실행
//	    try {
//	        String impUid = (String) payload.get("imp_uid"); // 예: 아임포트에서 전달받은 UID
//	        log.info("Processing payment cancellation for impUid: {}", impUid);
//	        
//	        Integer resId = paymentService.getResIdByImpUid(impUid); // imp_uid를 사용하여 resId 조회
//	        log.info("Found reservation ID: {}", resId);
//	        
//	        if (resId != null) {
//	            paymentService.updateReservationState(resId, 2); // 상태를 '예약 취소'로 변경
//	            log.info("Reservation status updated to canceled for resId: {}", resId);
//	            return ResponseEntity.ok("Reservation status updated to canceled");
//	        } else {
//	        	log.warn("No reservation found for impUid: {}", impUid);
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found");
//	        }
//	    } catch (Exception e) {
//	        log.error("Error processing payment cancellation webhook", e);
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating reservation status");
//	    }
//	}
    
    
    
}