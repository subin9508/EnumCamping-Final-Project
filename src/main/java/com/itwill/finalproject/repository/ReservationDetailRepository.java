package com.itwill.finalproject.repository;

import java.util.List;
import java.util.Optional;

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

	 // RdId에 해당하는 물품 리스트 조회
    ReservationDetail findByRdId(Long rdId);
//

    @Query("select new com.itwill.finalproject.dto.ReservationDetailDto(rd.reservationMaster.resId, i.itemId, " +
    	       "rd.itemQuantity, rd.itemAmount, i.itemName, i.itemImg) " +
    	       "from ReservationDetail rd join rd.item i " +
    	       "where rd.reservationMaster.resId = :resId")
    	List<ReservationDetailDto> findDetailsByResId(@Param("resId") Integer resId);



    
    
    //같은 resId를 가진 rdId찾기
    @Query("SELECT rd.rdId FROM ReservationDetail rd WHERE rd.reservationMaster.resId = :resId")
    List<Long> findRdIdByResId(@Param("resId") Integer resId);
    
    
//    // userId에 해당하는 예약 상세 정보 조회
//    List<ReservationListDto> findByUserId(String userId);
    
    
    //------- 예약 변경 -------
    // ReservationMaster와 ItemId로 ReservationDetail 찾기
    Optional<ReservationDetail> findByReservationMasterAndItem_ItemId(ReservationMaster reservationMaster, Integer itemId);

    // 특정 ReservationMaster에 해당하는 모든 ReservationDetail 찾기
    List<ReservationDetail> findByReservationMaster(ReservationMaster reservationMaster);

    // ReservationMaster의 ID로 모든 ReservationDetail 찾기
    @Query("SELECT rd FROM ReservationDetail rd WHERE rd.reservationMaster.resId = :resId")
    List<ReservationDetail> findByReservationMasterId(@Param("resId") Integer resId);

    // ReservationDetail의 총 금액 계산
    @Query("SELECT SUM(rd.itemAmount) FROM ReservationDetail rd WHERE rd.reservationMaster.resId = :resId")
    Integer calculateTotalAmount(@Param("resId") Integer resId);

    // ReservationMaster와 ItemId로 ReservationDetail 찾기 (DTO 반환)
    @Query("SELECT new com.itwill.finalproject.dto.ReservationDetailDto(rd.rdId, rd.item.itemId, rd.itemQuantity, rd.itemAmount, rd.item.itemName, rd.item.itemImg) " +
           "FROM ReservationDetail rd " +
           "WHERE rd.reservationMaster = :reservationMaster AND rd.item.itemId = :itemId")
    Optional<ReservationDetailDto> findDtoByReservationMasterAndItemId(@Param("reservationMaster") ReservationMaster reservationMaster, @Param("itemId") Integer itemId);

    // 특정 ReservationMaster에 해당하는 모든 ReservationDetail 찾기 (DTO 리스트 반환)
    @Query("SELECT new com.itwill.finalproject.dto.ReservationDetailDto(rd.rdId, rd.item.itemId, rd.itemQuantity, rd.itemAmount, rd.item.itemName, rd.item.itemImg) " +
           "FROM ReservationDetail rd " +
           "WHERE rd.reservationMaster.resId = :resId")
    List<ReservationDetailDto> findDtosByReservationMasterId(@Param("resId") Integer resId);

    // 특정 ReservationMaster의 특정 Item에 대한 ReservationDetail 업데이트
    @Modifying
    @Query("UPDATE ReservationDetail rd SET rd.itemQuantity = :quantity, rd.itemAmount = :amount " +
           "WHERE rd.reservationMaster.resId = :resId AND rd.item.itemId = :itemId")
    int updateReservationDetail(@Param("resId") Integer resId, @Param("itemId") Integer itemId, 
                                @Param("quantity") Integer quantity, @Param("amount") Integer amount);
}
	
