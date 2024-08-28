package com.itwill.finalproject.dto;

import lombok.Data;

@Data
public class ReservationItemUpdateDto {
    private Integer itemId;
    private Integer newQuantity;
}