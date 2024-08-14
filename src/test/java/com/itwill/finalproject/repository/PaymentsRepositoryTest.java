package com.itwill.finalproject.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.Payments;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@SpringBootTest
@Transactional // 테스트 이후 데이터 롤백을 위해 사용
public class PaymentsRepositoryTest {
	
	@Autowired
	private PaymentsRepository paymentsRepo;
	
	@Autowired
	private ReservationMasterRepository reservationMasterRepo;
	
	@Autowired 
	private UserRepository userRepo;
	
	
//	@Test
	public void testFindByResId() {
	    // 테스트용 User 생성 및 저장
	    User user = User.builder()
	    		.userId("subin")
	    		.userEmail("test@test.com")
	    		.userId("testUser")
	    		.userPassword("test1234")
	    		.userPhone("010-1234-1234")
	    		.userRole(1)
	    		.userState(1)
	    		.build();
	   
	    // 다른 필요한 필드들도 설정
	    user = userRepo.save(user);

	    // 테스트용 ReservationMaster 생성 및 저장 (ReservationMaster와 User 연결)
	    ReservationMaster reservation = ReservationMaster.builder()
	            .user(user)  // User 설정 (userKey 값)
	            .resCheckIn(LocalDate.of(2024, 8, 12))
	            .resCheckOut(LocalDate.of(2024, 8, 13))
	            .resCreatedTime(LocalDateTime.now())
	            .resModifiedTime(LocalDateTime.now())
	            .resTotalPrice(1000)
	            .resState(0)  // 예시로 상태를 설정
	            .build();
	    reservation = reservationMasterRepo.save(reservation);
	    
	    Integer resId = reservation.getResId();
		
		
		
		
		// 테스트옹 reservationMaster 생성 및 저장
		ReservationMaster reservation2 = new ReservationMaster();
		reservation.setResState(0); // 예시로 상태를 설정함.
		reservation = reservationMasterRepo.save(reservation);
		
		Integer resId2 = reservation.getResId();
		
		// 테스트용 Payments 생성 및 저장
		Payments payments = Payments.builder()
				.impUid("imp_123456789")
				.pgTid("pgT_123456789")
				.resTotalPrice(1000)
				.payStatus("paid")
				.resId(resId)
				.build();
		
		payments = paymentsRepo.save(payments);
		
		// 실제 메서드 호출 (resId로 결제 정보 검색)
		Optional<Payments> retrievedPayment = paymentsRepo.findByResId(resId);
		
		// 결과 확인
		assertThat(retrievedPayment).isPresent();
		assertThat(retrievedPayment.get().getImpUid()).isEqualTo("imp_123456789");
		assertThat(retrievedPayment.get().getResTotalPrice()).isEqualTo(1000);
		
		log.info("Retrieved Payment: {}", retrievedPayment.get());
	}
	
	
}
