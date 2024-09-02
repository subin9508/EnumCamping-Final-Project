package com.itwill.finalproject.domain;

import java.io.Serializable;
import java.util.Objects;

public class ClaimMasterId implements Serializable {

    private int clmId;
    private int resId;

    // 기본 생성자
    public ClaimMasterId() {}

    public ClaimMasterId(int clmId, int resId) {
        this.clmId = clmId;
        this.resId = resId;
    }

    // equals()와 hashCode() 메소드 재정의
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimMasterId that = (ClaimMasterId) o;
        return clmId == that.clmId && resId == that.resId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clmId, resId);
    }
}