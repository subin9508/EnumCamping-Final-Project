package com.itwill.finalproject.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.ReservationMaster;

public interface ReservationMasterRepository extends JpaRepository<ReservationMaster, Integer> {
	
	// 예약된 구역(아이템)
	@Query("select rd.item.id "
            + "from ReservationMaster rm "
            + "join ReservationDetail rd on rm.id = rd.reservationMaster.id "
            + "where (rm.resCheckIn = :resCheckIn and rm.resState = 1) "
                + "or (rm.resCheckIn = :resCheckIn and rm.resCheckOut - rm.resCheckIn = 2 and rm.resState = 1)")
	List<Integer> selectByResCheckIn(@Param("resCheckIn") LocalDate resCheckIn);
	
	// 구역 선택 후 가능한 night 수
	@Query("select rm "
            + "from ReservationMaster rm "
            + "join ReservationDetail rd on rm.id = rd.reservationMaster.id "
            + "where rd.item.id = :itemId "
            + "and rm.resCheckIn = :resCheckIn")
	List<ReservationMaster> selectByItemIdAndResCheckIn(@Param("itemId") int itemId, @Param("resCheckIn") LocalDate resCheckIn);
	
	// userId에 해당하는 reservation_master
	@Query("select rm from ReservationMaster rm "
			+ "where rm.user.userKey = :userKey "
			+ "and rm.resState = 0")
	ReservationMaster selectMasterByUserKey(@Param("userKey") Integer userKey);
	
	// order 페이지 넘어가기전 이전 내역 삭제
	@Modifying
	@Query("DELETE FROM ReservationMaster rm "
			+ "where rm.user.userKey = :userKey and rm.resState = 0")
	int deleteByUserId(@Param("userKey") Integer userKey);
	
	//userId로 찾기
	@Query("select rm from ReservationMaster rm "
			+ "where rm.user.userId = :userId "
			+ "order by id desc" )
	List<ReservationMaster> selectMasterByUserId(@Param("userId") String userId);
	
	// resId로 reservationMaster 찾기
    @Query("SELECT rm FROM ReservationMaster rm WHERE rm.resId = :resId")
    ReservationMaster findByResId(@Param("resId") Integer resId);

	//특가 예약 여부 체크
	@Query("select rm from ReservationMaster rm "
			+ "where rm.user.userId = :userId and rm.resCreatedTime >= :createdTime "
			+ "and rm.resState in (1, 3) and rm.resSpecial = 1")
	ReservationMaster selectSpecialPriceReservations(@Param("userId") String userId, @Param("createdTime") LocalDateTime createdTime);

//    @Query("select rm from ReservationMaster rm "
//    	     + "where rm.user.userId = :userId and rm.resCreatedTime >= :createdTime "
//    	     + "and rm.resState in (1, 3)")
//    	Optional<ReservationMaster> selectSpecialPriceReservations(@Param("userId") String userId, @Param("createdTime") LocalDateTime createdTime);
    
	@Modifying
	@Query("UPDATE ReservationMaster rm "
			+ "SET rm.resModifiedTime = CURRENT_TIMESTAMP "
			+ "where rm.resId = :resId")
	int updateResModifiedTime(@Param("resId") Integer resId);
	
	

}
