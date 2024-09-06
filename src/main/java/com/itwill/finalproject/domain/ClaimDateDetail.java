package com.itwill.finalproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class ClaimDateDetail {

    // 첫 번째 PK: cdd_id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가 설정
    private int cddId;

    private int clmId;

    private LocalDate canCheckIn;  // timestamp 대응
    private LocalDate canCheckOut; // timestamp 대응

    private int price;

    // 기본 생성자
    public ClaimDateDetail() {}

    // 모든 필드를 포함한 생성자
    public ClaimDateDetail(int cddId, int clmId, LocalDate canCheckIn, LocalDate canCheckOut, int price) {
        this.cddId = cddId;
        this.clmId = clmId;
        this.canCheckIn = canCheckIn;
        this.canCheckOut = canCheckOut;
        this.price = price;
    }

}
