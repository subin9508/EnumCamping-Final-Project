package com.itwill.finalproject.dto;

import lombok.Data;

@Data
public class RefundRequestDto {
    private Integer resId;
    private Integer amount;
    private String reason;
    
    public RefundRequestDto(Integer resId, Integer amount) {
        this.resId = resId;
        this.amount = amount;
    }
}