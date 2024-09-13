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
        
        List<Items> items = adminSvc.getAllZonesWithLatestPrice(); // 최신 가격이 포함된 zones 리스트 가져오기
        model.addAttribute("items", items);
        
        // 아이템 데이터 확인 
        for (Items item : items) {
            log.info("Item ID: {}, Name: {}, Price: {}", item.getItemId(), item.getItemName(), item.getItemPrice());
        }
        
        return "admin/price/zones";  // 해당 뷰로 리턴
    }
    
    @GetMapping("/price/items")
    public String itemPrice(Model model) {
        log.info("itemPrice");
        
        List<Items> items = adminSvc.getAllItemsWithLatestPrice(); // 최신 가격이 포함된 items 리스트 가져오기
        model.addAttribute("items", items);
        
        // 아이템 데이터 확인 
        for (Items item : items) {
            log.info("Item ID: {}, Name: {}, Price: {}", item.getItemId(), item.getItemName(), item.getItemPrice());
        }
        
        return "admin/price/items";  // 해당 뷰로 리턴
        
    }
	
	
    @PostMapping("/price/zones/update")
    public String updateZones(@RequestParam Map<String, String> allParams) {
    	log.info("updateZones : {}",allParams);
        allParams.forEach((key, value) -> {
            if (key.startsWith("price_")) {
                Integer itemId = Integer.parseInt(key.substring(6));
                BigDecimal newPrice = new BigDecimal(value);
                
                log.info("itemId = {}, newPrice = {}",itemId,newPrice);
                
                adminSvc.updateZoneDetails(itemId, newPrice); // 가격 + 특가 여부 업데이트
            }
        });
        return "redirect:/admin/price/zones"; // 해당 페이지로 리다이렉트
    }


    @PostMapping("/price/items/update")
    public String updateItems(@RequestParam Map<String, String> allParams, Model model) {
        log.info("updateItems : {}", allParams);
        allParams.forEach((key, value) -> {
            if (key.startsWith("price_")) {
                Integer itemId = Integer.parseInt(key.substring(6));
                BigDecimal newPrice = new BigDecimal(value);

                String newDesc = allParams.get("desc_" + itemId); // 설명 업데이트를 위한 추가 파라미터
                log.info("itemId = {}, newPrice = {}, newDesc = {}", itemId, newPrice, newDesc);

                adminSvc.updateItemDetails(itemId, newPrice, newDesc); // 가격과 설명 업데이트
            }
        });

        // 업데이트 후 최신 데이터를 다시 가져와서 모델에 추가
        List<Items> updatedItems = reservationSvc.getAllItems();
        model.addAttribute("items", updatedItems);

        List<Integer> latestPricesWithSpecialZero = adminSvc.getLatestPricesWithSpecialZero();
        model.addAttribute("latestPricesWithSpecialZero", latestPricesWithSpecialZero);

        return "admin/price/items"; // 뷰로 리턴하여 갱신된 데이터를 보여줌
    }
    
    
    @GetMapping("/price/zones/percentupdate")
    public void updateZonePercent(@RequestParam("percent") String percent) {
    	log.info("update percent = {}",percent);
    	
    }
    
 
	
}