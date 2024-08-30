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
    public void zonePrice(Model model) {
        log.info("zonePrice");
        List<Items> items = reservationSvc.getAllZones();
        model.addAttribute("items", items);
    }
    
    @GetMapping("/price/items")
    public void itemPrice(Model model) {
        log.info("itemPrice");
        List<Items> items = reservationSvc.getAllItems();
        model.addAttribute("items", items);
    }
	
	
    @PostMapping("/price/zones/update")
    public String updateZones(@RequestParam Map<String, String> allParams) {
    	log.info("updateZones : {}",allParams);
        allParams.forEach((key, value) -> {
            if (key.startsWith("price_")) {
                Integer itemId = Integer.parseInt(key.substring(6));
                BigDecimal newPrice = new BigDecimal(value);
                String newCheck = allParams.get("select_" + itemId);
                log.info("itemId = {}, newPrice = {}, newCheck={}",itemId,newPrice,newCheck);
                adminSvc.updateZoneDetails(itemId, newPrice,newCheck); // 가격 + 특가 여부 업데이트
            }
        });
        return "redirect:/admin/price/zones"; // 해당 페이지로 리다이렉트
    }

    @PostMapping("/price/items/update")
    public String updateItems(@RequestParam Map<String, String> allParams) {
    	log.info("updateItems : {}",allParams);
        allParams.forEach((key, value) -> {
            if (key.startsWith("price_")) {
                Integer itemId = Integer.parseInt(key.substring(6));
                BigDecimal newPrice = new BigDecimal(value);
                String newDesc = allParams.get("desc_" + itemId); // 설명 업데이트를 위한 추가 파라미터
                String newCheck = allParams.get("select_" + itemId);
                log.info("itemId = {}, newPrice = {}, newDesc = {}, newCheck={}",itemId,newPrice, newDesc,newCheck);
                adminSvc.updateItemDetails(itemId, newPrice, newDesc, newCheck); // 가격과 설명 업데이트
            }
        });
        return "redirect:/admin/price/items"; // 해당 페이지로 리다이렉트
    }
    
    
    @GetMapping("/price/zones/percentupdate")
    public void updateZonePercent(@RequestParam("percent") String percent) {
    	log.info("update percent = {}",percent);
    	
    }
    
	
}