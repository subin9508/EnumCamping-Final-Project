package com.itwill.finalproject.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ReservationMaster;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.dto.ReservationDetailDto;
import com.itwill.finalproject.repository.ItemsHistoryRepository;
import com.itwill.finalproject.repository.ItemsRepository;
import com.itwill.finalproject.repository.SpecialRepository;
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
	private final ItemsHistoryRepository itemsHistoryRepo;
	private final SpecialRepository spclRepo;
	
	@GetMapping("/calendar")
	public void reservationCalendar(HttpSession session,Model model) {
		log.info("reservationCalendar");
		List<Items> items = reservationSvc.getAllItems();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName(); // 사용자 ID 또는 사용자 이름
		User user = userSvc.read(userId);
		Integer userKey = user.getUserKey();
	    log.debug("userKey={}", userKey);
		
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
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String userId = authentication.getName();
	    log.debug("Authenticated userId: {}", userId);
	    
	    
		//특가기간인지 먼저 체크
		//아이템에 적힌 특가 기간이랑 현재 날짜를 비교하면되려나?
		//특가테이블에서 id가 같고 now가 기간내인 거 찾기
	    
	    //현재 시간 체크
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dateNow = now.format(formatter);
		log.debug("dateNow = {}",dateNow);
		
		
		//특가 상품인지 체크 & 시작날짜 찾기
		LocalDateTime startDate = spclRepo.findStartDate(itemId, now);
		//null이면 정상가, 날짜 반환되면 특가
        log.debug("시작날짜 =  {}", startDate);
        
        
        // 정상가- history에서 최신 0 가져오기
        if(startDate == null) {
        	log.info("아이템 {}는 특가 기간이 아닙니다.",itemId);
        	Integer itemPrice =itemsHistoryRepo.findNewestNormalPrice(itemId);
        	return new ResponseEntity<Integer>(itemPrice, HttpStatus.OK);
        } else { //특가
        	log.info("아이템 {}는 특가 기간입니다.",itemId);
        	// 특가 예약 조회
        	ReservationMaster rm = reservationSvc.findSpecial(userId, startDate);
        	log.info("rm = {}",rm);
        	
        	if (rm == null) {
        		log.info("특가 예약 안함");
        		//item table에서 특가 찾기 
        		Integer itemPrice = reservationSvc.readItemPrice(itemId);
        		if (itemPrice == -1) { //item 테이블에서 special이 0인 경우
        			itemPrice = reservationSvc.readSpecialPrice(itemId); //그 경우는 history에서 특가 찾기
        		}
        		log.debug("item price: {}", itemPrice);
        		
        		//에러잡는용도
        		if (itemPrice != null) {
        			return new ResponseEntity<Integer>(itemPrice, HttpStatus.OK);
        		} else {
        			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        		}
        	} else {
        		log.info("특가 예약 함");
        		//history에서 
        		LocalDateTime latestStartDate = reservationSvc.getLatestStartDate(itemId);
        		log.info("latestStartDate={}", latestStartDate);
        		if (latestStartDate == null) {
        			log.warn("No item history found for itemId: {}", itemId);
        			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        		}
        		
        		LocalDateTime targetDateTime = latestStartDate.minusSeconds(1);
        		log.debug("Computed targetDateTime by subtracting one second: {}", targetDateTime);
        		
        		Integer itemPrice = reservationSvc.getItemPriceByAdjustedEndDate(itemId, targetDateTime);
        		log.debug("Fetched item price for adjusted end date: {}", itemPrice);
        		
        		return itemPrice != null ? new ResponseEntity<>(itemPrice, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        	}
        }
	}
	
	// 예약확인 페이지
			@GetMapping("/order")
			public String showOrderPage(HttpSession session, Model model) {
				 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			     String userId = authentication.getName();
//			    String userId = (String) session.getAttribute("signedInUser");
			    User user = userSvc.read(userId);
			    Integer userKey = user.getUserKey();
			    log.info("user={}",user);
			    ReservationMaster reservationMaster = reservationSvc.getReservationMasterByUserKey(userKey);
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
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		  
		       String userId = authentication.getName(); // 사용자 ID 또는 사용자 이름
		        // 인증된 사용자에 대한 처리
//		    String userId = (String) session.getAttribute("signedInUser");
		    
		    log.debug("userId={}", userId);
		    User user = userSvc.read(userId);
		    model.addAttribute("user", user);
		    log.debug("user={}", user);
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
		        return "reservation/order";
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
		    reservationMaster = reservationSvc.getReservationMasterByUserKey(userKey);
		    // 예약 상세정보 가져오기
		    List<ReservationDetailDto> updatedReservationDetails = reservationSvc.getReservationDetailsByUserId(userKey);
		    
		    // 모델에 데이터 추가
		    model.addAttribute("reservationMaster", reservationMaster);
		    model.addAttribute("reservationDetails", updatedReservationDetails);
		    
		    return "reservation/order";
		}
		

}
