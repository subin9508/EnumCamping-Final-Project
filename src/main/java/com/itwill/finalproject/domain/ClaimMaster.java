package com.itwill.finalproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.PrePersist;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class ClaimMaster{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clmId;

    @Column(nullable = false)
    private Integer resId;

    @Column(length = 100)
    private String reason;

    private LocalDateTime cancelTime;

    private int totalPrice;

    @Column(length = 100)
    private String clmChange;

    // 기본 생성자
    public ClaimMaster() {}

    // 모든 필드를 포함한 생성자 (clmId 제외)
    public ClaimMaster(Integer resId, String reason, LocalDateTime cancelTime, int totalPrice, String clmChange) {
        this.resId = resId;
        this.reason = reason;
        this.cancelTime = cancelTime;
        this.totalPrice = totalPrice;
        this.clmChange = clmChange;
    }

}
