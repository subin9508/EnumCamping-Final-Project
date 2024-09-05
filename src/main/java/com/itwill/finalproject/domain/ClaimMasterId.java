package com.itwill.finalproject.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClaimMasterId implements Serializable {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clmId;
	
    private Integer resId;

    // 기본 생성자
    public ClaimMasterId() {}

    public ClaimMasterId(Integer clmId, Integer resId) {
        this.clmId = clmId;
        this.resId = resId;
    }

    // Getters and Setters
    public Integer getClmId() {
        return clmId;
    }

    public void setClmId(Integer clmId) {
        this.clmId = clmId;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    // equals()와 hashCode() 메소드 재정의
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimMasterId that = (ClaimMasterId) o;
        return Objects.equals(clmId, that.clmId) && Objects.equals(resId, that.resId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clmId, resId);
    }
}
