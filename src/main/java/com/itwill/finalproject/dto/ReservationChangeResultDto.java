package com.itwill.finalproject.dto;

import java.time.LocalDate;
import java.util.List;

import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;

import lombok.Data;

@Data
public class ReservationChangeResultDto {
    private Integer resId;
    private LocalDate oldCheckIn;
    private LocalDate oldCheckOut;
    private LocalDate newCheckIn;
    private LocalDate newCheckOut;
    private Integer oldTotalPrice;
    private Integer newTotalPrice;
    private List<ItemChangeDto> itemChanges;
    private Integer priceDifference;
    
 // 새로 추가된 필드
    private ReservationMaster updatedReservation;
    private User user;
    
    // 기존 필드들에 대한 getter와 setter는 @Data 어노테이션으로 자동 생성됩니다.
    
    // updatedReservation과 user에 대한 getter와 setter
    public ReservationMaster getUpdatedReservation() {
        return updatedReservation;
    }
    
    public void setUpdatedReservation(ReservationMaster updatedReservation) {
        this.updatedReservation = updatedReservation;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
}