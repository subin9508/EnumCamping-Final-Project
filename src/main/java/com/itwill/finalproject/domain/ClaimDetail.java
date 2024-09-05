package com.itwill.finalproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

// 복합 키를 사용하기 위해 IdClass 어노테이션을 이용합니다.

@Data
@Entity
public class ClaimDetail {

    // 첫 번째 PK: cd_id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가 설정
    private Integer cdId;

    // 두 번째 PK: clm_id
    @Column(nullable = false)
    private int clmId;

    // 세 번째 PK: res_id
    @Column(nullable = false)
    private int resId;

    private int itemId;
    private int itemQuantity;
    private int itemAmount;

    @Column(length = 100)  // varchar(100) 대응
    private String itemChange;

    // 기본 생성자
    public ClaimDetail() {}

    // 모든 필드를 포함한 생성자
    public ClaimDetail(int cdId, int clmId, int resId, int itemId, int itemQuantity, int itemAmount, String itemChange) {
        this.cdId = cdId;
        this.clmId = clmId;
        this.resId = resId;
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
        this.itemAmount = itemAmount;
        this.itemChange = itemChange;
    }

}
