package com.itwill.finalproject.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ItemsHistory;
import com.itwill.finalproject.service.AdminService;
import com.itwill.finalproject.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final ReservationService reservationSvc;
	private final AdminService adminSvc;
	
	
    @GetMapping("/price/zones")
    public String zonePrice(Model model) {
        log.info("zonePrice");
        
        // 모든 zones를 가져옴
        List<Items> items = reservationSvc.getAllZones();
        model.addAttribute("items", items);
        
        // 특가가 없는 상태에서 가장 최신 가격을 가져옴
        List<Integer> latestPricesWithSpecialZero = adminSvc.getLatestPricesWithSpecialZero();
        model.addAttribute("latestPricesWithSpecialZero", latestPricesWithSpecialZero);

        return "admin/price/zones";  // 해당 뷰로 리턴
    }
    
    @GetMapping("/price/items")
    public String itemPrice(Model model) {
        log.info("itemPrice");
        
        List<Items> items = reservationSvc.getAllItems();
        model.addAttribute("items", items);
        
        // 특가가 없는 상태에서 가장 최신 가격을 가져옴
        List<Integer> latestPricesWithSpecialZero = adminSvc.getLatestPricesWithSpecialZero();
        model.addAttribute("latestPricesWithSpecialZero", latestPricesWithSpecialZero);

        return "admin/price/items";  // 해당 뷰로 리턴
        
    }
	
	
    @PostMapping("/price/zones/update")
    public String updateZones(@RequestParam Map<String, String> allParams) {
    	log.info("updateZones : {}",allParams);
        allParams.forEach((key, value) -> {
            if (key.startsWith("price_")) {
                Integer itemId = Integer.parseInt(key.substring(6));
                BigDecimal newPrice = new BigDecimal(value);
                
                // 체크박스 상태를 가져오고, 체크되지 않았으면 기본값을 "off"로 설정
                String newCheck = allParams.get("select_" + itemId);
                String special = "0"; // 기본값으로 '체크 안됨' 상태로 설정
                if (newCheck != null && newCheck.equals("on")) {
                    special = "1"; // 체크된 상태
                }
                
                log.info("itemId = {}, newPrice = {}, newCheck={}, special={}",itemId,newPrice,special);
                
                adminSvc.updateZoneDetails(itemId, newPrice, special); // 가격 + 특가 여부 업데이트
            }
        });
        return "redirect:/admin/price/zones"; // 해당 페이지로 리다이렉트
    }

//    @PostMapping("/price/items/update")
//    public String updateItems(@RequestParam Map<String, String> allParams) {
//    	log.info("updateItems : {}",allParams);
//        allParams.forEach((key, value) -> {
//            if (key.startsWith("price_")) {
//                Integer itemId = Integer.parseInt(key.substring(6));
//                BigDecimal newPrice = new BigDecimal(value);
//                
//                String newDesc = allParams.get("desc_" + itemId); // 설명 업데이트를 위한 추가 파라미터
//                String newCheck = allParams.getOrDefault("select_" + itemId, "off");
//                log.info("itemId = {}, newPrice = {}, newDesc = {}, newCheck={}",itemId,newPrice, newDesc,newCheck);
//                
//                adminSvc.updateItemDetails(itemId, newPrice, newDesc, newCheck); // 가격과 설명 업데이트
//            }
//        });
//        return "redirect:/admin/price/items"; // 해당 페이지로 리다이렉트
//    }
    
    @PostMapping("/price/items/update")
    public String updateItems(@RequestParam Map<String, String> allParams, Model model) {
        log.info("updateItems : {}", allParams);
        allParams.forEach((key, value) -> {
            if (key.startsWith("price_")) {
                Integer itemId = Integer.parseInt(key.substring(6));
                BigDecimal newPrice = new BigDecimal(value);

                String newDesc = allParams.get("desc_" + itemId); // 설명 업데이트를 위한 추가 파라미터
                String newCheck = allParams.getOrDefault("select_" + itemId, "off");
                log.info("itemId = {}, newPrice = {}, newDesc = {}, newCheck={}", itemId, newPrice, newDesc, newCheck);

                adminSvc.updateItemDetails(itemId, newPrice, newDesc, newCheck); // 가격과 설명 업데이트
            }
        });

        // 업데이트 후 최신 데이터를 다시 가져와서 모델에 추가
        List<Items> updatedItems = reservationSvc.getAllItems();
        model.addAttribute("items", updatedItems);

        List<Integer> latestPricesWithSpecialZero = adminSvc.getLatestPricesWithSpecialZero();
        model.addAttribute("latestPricesWithSpecialZero", latestPricesWithSpecialZero);

        return "admin/price/items"; // 해당 페이지로 리다이렉트
    }
    
    
    @GetMapping("/price/zones/percentupdate")
    public void updateZonePercent(@RequestParam("percent") String percent) {
    	log.info("update percent = {}",percent);
    	
    }
    
//    @GetMapping("/getLatestPriceWithSpecialZero")
//    public List<ItemsHistory> getLatestPriceWithSpecialZero() {
//        return adminSvc.getLatestPricesWithSpecialZero();
//    }
        
	
}