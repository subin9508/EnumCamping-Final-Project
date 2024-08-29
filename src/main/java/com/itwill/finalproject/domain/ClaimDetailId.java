package com.itwill.finalproject.domain;

import java.io.Serializable;
import java.util.Objects;

public class ClaimDetailId implements Serializable {

    private int cdId;
    private int clmId;
    private int resId;
    private int rdId;

    // 기본 생성자
    public ClaimDetailId() {}

    public ClaimDetailId(int cdId, int clmId, int resId, int rdId) {
        this.cdId = cdId;
        this.clmId = clmId;
        this.resId = resId;
        this.rdId = rdId;
    }

    // equals()와 hashCode() 메소드 재정의
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimDetailId that = (ClaimDetailId) o;
        return cdId == that.cdId && clmId == that.clmId && resId == that.resId && rdId == that.rdId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cdId, clmId, resId, rdId);
    }
}