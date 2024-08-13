package com.itwill.finalproject.web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.repository.ItemsRepository;
import com.itwill.finalproject.repository.UserRepository;
import com.itwill.finalproject.service.ReservationService;
import com.itwill.finalproject.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
	
	private final ReservationService reservationSvc;
	private final UserService userSvc;
	private final UserRepository userRepo;
	private final ItemsRepository itemsRepo;
	
	@GetMapping("/calendar")
	public void reservationCalendar(Model model) {
		log.info("reservationCalendar");
		List<Items> items = reservationSvc.getAllItems();
		
		for (Items item : items) {
			log.info("Item: {}", item);
		}
		model.addAttribute("items", items);
	}
	
	@GetMapping("/calendar/{date}")
	@ResponseBody
	public List<Integer> reservationCalendar(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		log.debug("GET: calendar with date {}", date);
		
		 // 해당 날짜에 예약된 구역 ID 목록을 가져옵니다.
        List<Integer> reservedAreaIds = reservationSvc.readReservedAreas(date);
        return reservedAreaIds;
	}
	
	@GetMapping("/calendar/{date}/{area}")
	public ResponseEntity<List<ReservationMaster>> reservationCalendar(@PathVariable("date") String date, @PathVariable("area") int area) {
		LocalDate checkInDate = LocalDate.parse(date);
		log.debug("GET: calendar with date and area {}, {}", date, area);
		List<ReservationMaster> reservations = reservationSvc.readReservationMaster(checkInDate, area);
		
		return new ResponseEntity<>(reservations, HttpStatus.OK);
	}
	
	@GetMapping("/itemPrice/{itemId}") 
	public ResponseEntity<Integer> getItemPrice(@PathVariable("itemId") int itemId) {
		log.debug("GET: itemPrice with itemId {}", itemId);
		
		Integer itemPrice = reservationSvc.readItemPrice(itemId);
		if (itemPrice != null) {
			return new ResponseEntity<Integer>(itemPrice, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	// 예약확인 페이지
		@GetMapping("/order")
		public String showOrderPage(HttpSession session, Model model) {
		    String userId = (String) session.getAttribute("signedInUser");
		    User user = userSvc.read(userId);
		    Integer userKey = user.getUserKey();
		    
		    ReservationMaster reservationMaster = reservationSvc.getReservationMasterByUserId(userKey);
		    List<ReservationDetailDto> reservationDetails = reservationSvc.getReservationDetailsByUserId(userKey);

		    model.addAttribute("user", user);
		    model.addAttribute("reservationMaster", reservationMaster);
		    model.addAttribute("reservationDetails", reservationDetails);

		    return "/reservation/order";
		}
		
		@PostMapping("/order")
		public String getReservationList(
		        @RequestBody Map<String, Object> requestData, 
		        HttpSession session, 
		        Model model) {

		    log.debug("reservationList(requestData={})", requestData);
		    
		    // 세션에서 사용자 정보 가져오기
		    String userId = (String) session.getAttribute("signedInUser");
		    log.debug("userId={}", userId);
		    User user = userSvc.read(userId);
		    model.addAttribute("user", user);
		    
		    // User 객체에서 userKey 가져오기
		    Integer userKey = user.getUserKey();
		    log.debug("userKey={}", userKey);
		    model.addAttribute("userKey", userKey);
		    
		    // 기존 예약 상세 정보와 마스터 정보 삭제
		    try {
		        log.debug("Attempting to delete reservation master for userId: {}", userId);
		        reservationSvc.deleteReservationMaster(userKey);
		        log.info("Successfully deleted reservation master for userId: {}", userId);
		    } catch (Exception e) {
		        log.error("Failed to delete reservation master for userId: {}", userId, e);
		        return "/reservation/order";
		    }
		    
		    // requestData에서 reservationMaster와 reservationDetail 추출
		 // requestData에서 reservationMaster와 reservationDetail 추출
		    Map<String, Object> reservationMasterMap = (Map<String, Object>) requestData.get("reservationMaster");
		    List<Map<String, Object>> reservationDetailList = (List<Map<String, Object>>) requestData.get("reservationDetail");

		    log.debug("reservationMasterMap={}", reservationMasterMap);
		    log.debug("reservationDetailMap={}", reservationDetailList);
		    
		    if (reservationMasterMap == null || reservationDetailList == null) {
		        log.error("reservationMasterMap or reservationDetailList is null");
		        return "/reservation/order";
		    }
		    
		    // ReservationMaster 객체 생성 및 설정
		    ReservationMaster reservationMaster = new ReservationMaster();
		    reservationMaster.setUserById(userId, userRepo);
		    reservationMaster.setResCheckIn(LocalDate.parse((String) reservationMasterMap.get("resCheckIn")));
		    reservationMaster.setResCheckOut(LocalDate.parse((String) reservationMasterMap.get("resCheckOut")));
		    reservationMaster.setResTotalPrice((Integer) reservationMasterMap.get("resTotalPrice"));
		    reservationMaster.setRequirement((String)reservationMasterMap.get("requirement"));
		    
		    // ReservationDetailDto 객체 리스트 생성 및 설정
		    List<ReservationDetailDto> reservationDetails = new ArrayList<>();

		    for (Map<String, Object> detailMap : reservationDetailList) {
		        Integer itemId = (detailMap.get("itemId") != null) ? Integer.parseInt(detailMap.get("itemId").toString()) : null;
		        Integer itemQuantity = (detailMap.get("itemQuantity") != null) ? Integer.parseInt(detailMap.get("itemQuantity").toString()) : null;
		        Integer itemAmount = (detailMap.get("itemAmount") != null) ? Integer.parseInt(detailMap.get("itemAmount").toString()) : null;

		        log.debug("itemId={}, itemAmount={}, itemQuantity={}", itemId, itemAmount, itemQuantity);

		        if (itemId == null || itemAmount == null || itemQuantity == null) {
		            log.error("itemId, itemAmount, or itemQuantity is null");
		            return "/reservation/order";
		        }

		        ReservationDetailDto reservationDetail = new ReservationDetailDto();
		        reservationDetail.setItemId(itemId);
		        reservationDetail.setItemQuantity(itemQuantity);
		        reservationDetail.setItemAmount(itemAmount);
		        reservationDetails.add(reservationDetail);
		    }
		    
		    // 서비스 레이어를 통해 예약 생성
		    log.debug("Before calling makeReservation method");
		    reservationSvc.makeReservation(reservationMaster, reservationDetails);
		    log.debug("After calling makeReservation method");
		    
		    // 예약정보 가져오기
		    reservationMaster = reservationSvc.getReservationMasterByUserId(userKey);
		    // 예약 상세정보 가져오기
		    List<ReservationDetailDto> updatedReservationDetails = reservationSvc.getReservationDetailsByUserId(userKey);
		    
		    // 모델에 데이터 추가
		    model.addAttribute("reservationMaster", reservationMaster);
		    model.addAttribute("reservationDetails", updatedReservationDetails);
		    
		    return "/reservation/order";
		}
		
		@GetMapping("/reservationConfirm")
		public String reservationConfirm(@RequestParam(name = "resId") int resId, Model model) {
			log.debug("reservationConfirm()");
			
			ReservationMaster resMaster = userSvc.readReservationMasterDetails(resId).orElseThrow();
			List<ReservationDetailDto> resDetail = userSvc.readReservationDetails(resId);

			model.addAttribute("resMaster", resMaster);
			model.addAttribute("resDetail", resDetail);

			return "reservation/reservationConfirm";
		}


}
