package com.itwill.finalproject.dto;

import lombok.Data;

@Data
public class ItemChangeDto {
    private Integer itemId;
    private String itemName;
    private Integer oldQuantity;
    private Integer newQuantity;
    private Integer oldPrice;
    private Integer newPrice;
}