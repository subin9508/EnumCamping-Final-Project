package com.itwill.finalproject.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="itemshistory")
public class ItemsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    (name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID") // FK 제약조건이 있는 컬럼 이름.
    private Items items;
    
    @Basic(optional = false)
    @Column(name = "item_price")
    private Integer itemPrice;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private int special;

}