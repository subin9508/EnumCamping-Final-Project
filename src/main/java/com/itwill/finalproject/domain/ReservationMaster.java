package com.itwill.finalproject.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.itwill.finalproject.repository.UserRepository;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class ReservationMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer resId;
	
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;
	
	private String requirement;
	
	private String resCreatedTime;
	
	private LocalDateTime resModifiedTime;
	
	private LocalDate resCheckIn;
	
	private LocalDate resCheckOut;
	
	private Integer resTotalPrice;
	
	@Builder.Default
	private Integer resState = 0;  // JPA 엔티티에 기본값 설정
	
	@OneToMany(mappedBy = "reservationMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationDetail> reservationDetails;
	
	public void setUserById(String userId, UserRepository user) {
		this.user = user.findByUserId(userId).orElseThrow();
	}
	
}
