package com.itwill.finalproject.dto;

import lombok.Data;

@Data
public class AdditionalPaymentDto {
    private Integer resId;
    private Integer amount;
    private String payMethod;
    private String impUid;  // 결제 모듈에서 받은 고유 ID
    
    
    public AdditionalPaymentDto(Integer resId, Integer amount) {
        this.resId = resId;
        this.amount = amount;
    }

}