package com.itwill.finalproject.dto;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class PaymentsCancelDto {
	private Integer payId;
	private String impUid;
    private Integer canId;
    private Integer resId;
    private Integer canAmount;
    private LocalDateTime canDate;
    private String canRole;
    
    public PaymentsCancelDto() {
    	this.canDate = LocalDateTime.now(); // 취소 날짜 및 시간을 현재 시간으로 설정
    	this.canRole = "구매자"; // 초기값 설정
    }
	
}
