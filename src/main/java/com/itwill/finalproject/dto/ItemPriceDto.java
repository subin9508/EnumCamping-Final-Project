package com.itwill.finalproject.dto;


public class ItemPriceDto {
    private Integer itemId;
    private Integer itemPrice;
    
    public ItemPriceDto(Integer itemId, Integer itemPrice) {
        this.itemId = itemId;
        this.itemPrice = itemPrice;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getItemPrice() {
        return itemPrice;
    }
}
