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
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ReservationDetail;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;
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
		    
		    ReservationMaster reservationMaster = reservationSvc.getReservationMasterByUserId(userId);
		    List<ReservationDetail> reservationDetails = reservationSvc.getReservationDetailsByUserId(userId);

		    model.addAttribute("user", user);
		    model.addAttribute("reservationMaster", reservationMaster);
		    model.addAttribute("reservationDetails", reservationDetails);

		    return "reservation/order";
		}
		
		@PostMapping("/order")
		public String getReservationList
		(@RequestBody Map<String, Object> requestData, HttpSession session, Model model) {
		    log.debug("reservationList(requestData={})", requestData);
		        
		    // 세션에서 사용자 정보 가져오기
		    String userId = (String) session.getAttribute("signedInUser");
		    log.debug("userId={}", userId);
		    User user = userSvc.read(userId);
		    model.addAttribute("user", user);
		    
//		    reservationService.deleteReservationDetail(userId);
//		    reservationService.deleteReservationMaster(userId);

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
		    
		    // ReservationDetail 객체 생성 및 설정
		    
		    List<ReservationDetail> reservationDetails = new ArrayList<>();

		    for (Map<String, Object> detailMap : reservationDetailList) {
		        Object itemIdObj = detailMap.get("itemId");
		        Object itemAmountObj = detailMap.get("itemAmount");
		        Object itemQuantityObj = detailMap.get("itemQuantity");

		        log.debug("itemIdObj={}, itemAmountObj={}", itemIdObj, itemAmountObj);

		        if (itemIdObj == null || itemAmountObj == null) {
		            log.error("itemId or itemAmount is null");
		            return "/reservation/order";
		        }

		        ReservationDetail reservationDetail = new ReservationDetail();
		     // ItemsRepository에서 itemId로 Items 객체를 찾고 설정
		        Integer itemId = Integer.parseInt(itemIdObj.toString());
		        reservationDetail.setItemById(itemId, itemsRepo);
		        reservationDetail.setItemQuantity(Integer.parseInt(itemQuantityObj.toString()));
		        reservationDetail.setItemAmount(Integer.parseInt(itemAmountObj.toString()));
		        reservationDetails.add(reservationDetail);
		    }
		    
		    reservationSvc.makeReservation(reservationMaster, reservationDetails);

		    // 예약정보 가져오기
		    reservationMaster = reservationSvc.getReservationMasterByUserId(userId);
		 	 		
		 	// 예약 상세정보 가져오기
		 	 reservationDetails = reservationSvc.getReservationDetailsByUserId(userId);
		 	 	
		 	 model.addAttribute("reservationMaster", reservationMaster);
		 	 model.addAttribute("reservationDetails", reservationDetails);
		    
		    return "/reservation/order";
		}

}
