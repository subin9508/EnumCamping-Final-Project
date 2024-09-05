package com.itwill.finalproject.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payId;

    @Column(name = "imp_uid")
    private String impUid;

    @Column(name = "pg_tid")
    private String pgTid;

    @Column(name = "res_id")
    private Integer resId;

    @Column(name = "res_total_price")
    private Integer resTotalPrice;

    @Column(name = "pay_date")
    private LocalDateTime payDate;

    @Column(name = "pay_method")
    private String payMethod;

    @Column(name = "pay_status")
    private String payStatus;

    @Column(name = "buyer_email")
    private String buyerEmail;
}