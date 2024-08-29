package com.itwill.finalproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Data
@Entity
@IdClass(ClaimMasterId.class)
public class ClaimMaster {

    // 첫 번째 PK: clm_id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가 설정
    private int clmId;

    // 두 번째 PK: res_id
    @Id
    private int resId;

    @Column(length = 100)  // varchar(100) 대응
    private String reason;

    private LocalDateTime cancelTime;  // timestamp 대응

    private int totalPrice;

    @Column(length = 100)  // varchar(100) 대응
    private String clmChange;

    // 기본 생성자
    public ClaimMaster() {}

    // 모든 필드를 포함한 생성자
    public ClaimMaster(int clmId, int resId, String reason, LocalDateTime cancelTime, int totalPrice, String clmChange) {
        this.clmId = clmId;
        this.resId = resId;
        this.reason = reason;
        this.cancelTime = cancelTime;
        this.totalPrice = totalPrice;
        this.clmChange = clmChange;
    }

}
