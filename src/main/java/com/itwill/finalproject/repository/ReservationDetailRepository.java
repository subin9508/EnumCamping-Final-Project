package com.itwill.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.dto.ReservationDetailDto;

public interface ReservationDetailRepository extends JpaRepository<ReservationDetail, Integer>{
	// userId에 해당하는 reservation_detail
	// 특정 사용자 ID(userId)에 해당하는 예약 상세 정보를 조회
	@Query("select new com.itwill.finalproject.dto.ReservationDetailDto(rd.reservationMaster.resId, rd.item.itemId, rd.itemQuantity, rd.itemAmount, i.itemName, i.itemImg) "
			+ "from ReservationDetail rd "
			+ "join Items i on rd.item.itemId = i.itemId "
			+ "where rd.reservationMaster.resId = (select rm.resId from ReservationMaster rm where rm.user.userKey = :userKey and rm.resState = 0) "
			+ "order BY rd.item.itemId asc")
	List<ReservationDetailDto> selectDetailsByUserId(@Param("userKey") Integer userKey);
	
	// order 페이지에 넘어가기전 이전 내역 삭제
    // 특정 사용자 ID(userId)에 해당하는 예약 상태가 0인 예약 상세 정보를 삭제
	@Modifying
	@Query(value = "DELETE FROM reservation_detail rd WHERE rd.res_id IN " +
	               "(SELECT rm.res_id FROM reservation_master rm WHERE rm.userKey= :userKey AND rm.resState = 0)", 
	       nativeQuery = true)
	int deleteByResId(@Param("userKey") Integer userKey);

	 // ResId에 해당하는 물품 리스트 조회
    List<ReservationDetailDto> findByRdId(Integer Id);
    
//    // userId에 해당하는 예약 상세 정보 조회
//    List<ReservationListDto> findByUserId(String userId);
	
	
}
