package com.itwill.finalproject.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor 
@NoArgsConstructor 
@Builder
public class PaymentsCancel {
	
	@Column(name = "pay_id")
	private Integer payId;
	
	@Column(name = "imp_uid")
	private String impUid;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer canId;
	
	@Column(name = "res_id")
    private Integer resId;
    
	@Column(name = "can_amount")
    private Integer canAmount;
    
	@Column(name = "can_date")
    private LocalDateTime canDate;
    
	@Column(name = "can_role")
    private String canRole;
	
}