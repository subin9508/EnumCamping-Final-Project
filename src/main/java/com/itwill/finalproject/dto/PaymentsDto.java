package com.itwill.finalproject.dto;

import java.time.LocalDateTime;

import com.itwill.finalproject.domain.Payments;

import lombok.Data;

@Data
public class PaymentsDto {
	private Integer payId; // 결제 아이디
	private String impUid; // 아임포트 UID
	private String pgTid; // PG사 거래 ID
	private Integer resId; // 예약 ID
	private Integer resTotalPrice; // 결제 금액
	private LocalDateTime payDate; // 결제 날짜
	private String payMethod; // 결제 방법
	private String payStatus; // 결제 상태
	private String buyerEmail; // 구매자 이메일
	
	public Payments toEntity() {
		return Payments.builder()
				.payId(payId)
				.impUid(impUid)
				.pgTid(pgTid)
				.resId(resId)
				.resTotalPrice(resTotalPrice)
				.payDate(payDate)
				.payMethod(payMethod)
				.payStatus(payStatus)
				.buyerEmail(buyerEmail)
				.build();
	}

}
