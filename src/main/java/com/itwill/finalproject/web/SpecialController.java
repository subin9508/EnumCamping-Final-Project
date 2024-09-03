package com.itwill.finalproject.web;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ItemsHistory;
import com.itwill.finalproject.service.AdminService;
import com.itwill.finalproject.service.ReservationService;
import com.itwill.finalproject.service.SpecialService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SpecialController {

    private final ReservationService reservationSvc;
    private final AdminService adminSvc;
    private final SpecialService specialSvc;

    @GetMapping("/price/zonesSpecial")
    public String zonePrice(Model model) {
        log.info("zonePrice");

        // 모든 구역 리스트 가져오기
        List<Items> zones = reservationSvc.getAllZones();
        model.addAttribute("items", zones);

        // 최신 정상 가격 맵 가져오기
        Map<Integer, Integer> latestPricesWithSpecialZero = specialSvc.getLatestPricesWithSpecialZeroForZones();
        model.addAttribute("latestPricesWithSpecialZero", latestPricesWithSpecialZero);

        // 최신 특가 가격 리스트 가져오기
        Map<Integer, Integer> latestSpecialPrices = specialSvc.getLatestSpecialPricesForZones();
        Map<Integer, Integer> combinedPrices = new HashMap<>();

        for (Items zone : zones) {
            Integer itemId = zone.getItemId();
            Integer specialPrice = latestSpecialPrices.get(itemId);
            Integer normalPrice = latestPricesWithSpecialZero.getOrDefault(itemId, 0);

            if (zone.getSpecial() == 1 && specialPrice != null) {
                combinedPrices.put(itemId, specialPrice);
            } else {
                combinedPrices.put(itemId, normalPrice);
            }
        }

        model.addAttribute("combinedPrices", combinedPrices); // 모델에 추가
        return "admin/price/zonesSpecial";
    }
    
    @GetMapping("/price/itemsSpecial")
    public String itemPrice(Model model) {
        log.info("itemPrice");

        // 모든 아이템 리스트 가져오기
        List<Items> items = reservationSvc.getAllItems();
        model.addAttribute("items", items);

        // 최신 정상 가격 맵 가져오기
        Map<Integer, Integer> latestPricesWithSpecialZero = specialSvc.getLatestPricesWithSpecialZeroForItems();
        model.addAttribute("latestPricesWithSpecialZero", latestPricesWithSpecialZero);

        // 최신 특가 가격 리스트 가져오기
        List<Integer> latestSpecialPricesList = specialSvc.getLatestSpecialPricesForItems();

        // 특가 가격을 아이템 ID와 연결할 수 있는 맵 만들기
        Map<Integer, Integer> latestSpecialPrices = new HashMap<>();
        for (int i = 0; i < Math.min(items.size(), latestSpecialPricesList.size()); i++) {
            latestSpecialPrices.put(items.get(i).getItemId(), latestSpecialPricesList.get(i));
        }

        // 정상 가격과 특가 가격을 같이 매핑하는 로직
        Map<Integer, Integer> combinedPrices = new HashMap<>();
        for (Items item : items) {
            Integer itemId = item.getItemId();
            if (latestSpecialPrices.containsKey(itemId)) {
                // 특가가 존재하면 특가 가격을 사용
                combinedPrices.put(itemId, latestSpecialPrices.get(itemId));
            } else {
                // 특가가 없으면 정상 가격을 사용
                combinedPrices.put(itemId, latestPricesWithSpecialZero.getOrDefault(itemId, 0));
            }
        }

        model.addAttribute("combinedPrices", combinedPrices); // 모델에 추가
        return "admin/price/itemsSpecial";
    }

    @PostMapping("/price/zonesSpecial/update")
    public String updateZones(@RequestParam Map<String, String> allParams) {
        log.info("updateZones : {}", allParams);
        allParams.forEach((key, value) -> {
            try {
                if (key.startsWith("price_")) {
                    Integer itemId = Integer.parseInt(key.substring(6));
                    if (value != null && !value.isEmpty()) {
                        BigDecimal newPrice = new BigDecimal(value); // 정상 가격
                        
                        // 특가 가격에 대한 더 안전한 처리
                        String specialPriceStr = allParams.getOrDefault("specialPrice_" + itemId, value);
                        BigDecimal specialPrice = isNumeric(specialPriceStr) ? new BigDecimal(specialPriceStr) : newPrice;
                        
                        String newCheck = allParams.getOrDefault("select_" + itemId, "off");   // 특가 여부
                        
                        log.info("itemId = {}, newPrice = {}, specialPrice = {}, newCheck={}", itemId, newPrice, specialPrice, newCheck);
                        
                        if ("on".equals(newCheck)) {
                        	specialSvc.updateSpecialZoneDetails(itemId, specialPrice, "on"); // 특가 가격을 사용
                        } else {
                        	specialSvc.updateSpecialZoneDetails(itemId, newPrice, "off"); // 정상 가격을 사용
                        }
                    } else {
                        log.warn("Invalid price value for itemId: {}", itemId);
                    }
                }
            } catch (NumberFormatException ex) {
                log.error("Invalid number format for key: {}, value: {}", key, value, ex);
            }
        });
        return "redirect:/admin/price/zonesSpecial"; // 해당 페이지로 리다이렉트
    }
    
    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            new BigDecimal(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @PostMapping("/price/itemsSpecial/update")
    public String updateItems(@RequestParam Map<String, String> allParams) {
        log.info("updateItems : {}", allParams);
        allParams.forEach((key, value) -> {
            if (key.startsWith("price_")) {
                Integer itemId = Integer.parseInt(key.substring(6));
                BigDecimal newPrice = new BigDecimal(value);
                String newDesc = allParams.get("desc_" + itemId); // 설명 업데이트를 위한 추가 파라미터
                String newCheck = allParams.getOrDefault("select_" + itemId, "off"); // 기본값 'off' 설정
                log.info("itemId = {}, newPrice = {}, newDesc = {}, newCheck={}", itemId, newPrice, newDesc, newCheck);
                
                specialSvc.updateSpecialItemDetails(itemId, newPrice, newDesc, newCheck); // 가격과 설명 업데이트
            }
        });
        return "redirect:/admin/price/itemsSpecial"; // 해당 페이지로 리다이렉트
    }

    @PostMapping("/updateSpecialAndInsertRecord")
    public ResponseEntity<?> updateSpecialAndInsertRecord(@RequestBody Map<String, Object> payload) {
        try {
            // itemId를 String으로 받고, Integer로 변환
            String itemIdStr = (String) payload.get("itemId");
            Integer itemId = Integer.parseInt(itemIdStr);
            log.info("업데이트 itemId={}", itemId);
            
            // 서비스 클래스 메서드 호출
            specialSvc.updateSpecialEndDateAndInsertRecord(itemId);

            return ResponseEntity.ok().body("{\"message\": \"End date updated and new record inserted successfully.\"}");
        } catch (Exception e) {
            // 예외 발생 시 로그 기록
            log.error("Error updating end date and inserting new record: ", e);

            // 에러 응답을 JSON 형식으로 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"Error updating end date and inserting new record.\"}");
        }
    }
    
    
//    @GetMapping("/price/zones/percentupdate")
//    public void updateZonePercent(@RequestParam("percent") String percent) {
//    	log.info("update percent = {}",percent);
//    	
//    }
    
//    @GetMapping("/getLatestPriceWithSpecialZero")
//    public List<ItemsHistory> getLatestPriceWithSpecialZero() {
//        return adminSvc.getLatestPricesWithSpecialZero();
//    }
    
	
}