package com.itwill.finalproject.domain;

import com.itwill.finalproject.repository.ItemsRepository;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
	public class ReservationDetail {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer rdId;
		
		@ToString.Exclude
		@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
		@JoinColumn(name = "RES_ID")
		private ReservationMaster reservationMaster; // 예약 아이디
		
		@ToString.Exclude
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "ITEM_ID")
		private Items item; // 아이템 아이디
		
		private Integer itemQuantity; // 아이템 수량
		
		private Integer itemAmount; // 아이템 가격(단가 * 수량)
		
		// 헬퍼 메서드 추가
			public void setItemById(Integer itemId, ItemsRepository itemsRepository) {
				this.item = itemsRepository.findById(itemId)
				             .orElseThrow();
			}
	}
