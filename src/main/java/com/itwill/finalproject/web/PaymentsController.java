package com.itwill.finalproject.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.exception.ControllerException;
import com.itwill.finalproject.exception.ServiceException;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.service.PaymentsService;
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

    // 생성자를 통해 의존성 주입
    @Autowired
    public PaymentsController(PaymentsService paymentsService, UserService userService) {
        this.paymentsService = paymentsService;
        this.userService = userService;

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
    
    @GetMapping("/reservation/succeeded/{resId}")
    public String paymentSucceeded(@PathVariable("resId") Integer resId , Model model, HttpSession session) {
    	
    	Integer rdId = (Integer) session.getAttribute("rdId"); // 세션에서 rdId 가져오기
        Optional<ReservationMaster> resMaster = userService.readReservationMasterDetails(resId);
        List<ReservationDetailDto> resDetail = userService.readReservationDetails(rdId);
        
        String userId = (String) session.getAttribute("userId"); // 세션에서 userId 가져오기
        model.addAttribute("res_id", resId); // 모델에 resId 추가
        model.addAttribute("resMaster", resMaster);
        model.addAttribute("resDetail", resDetail);
        model.addAttribute("userId", userId); // 모델에 userId 추가

        return "reservation/succeeded"; // succeeded.html 파일을 가리킴
    }
}