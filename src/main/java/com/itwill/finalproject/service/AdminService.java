package com.itwill.finalproject.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ItemsHistory;
import com.itwill.finalproject.repository.ItemsHistoryRepository;
import com.itwill.finalproject.repository.ItemsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private ItemsHistoryRepository itemsHistoryRepository;
    
    @Autowired
    private ReservationService reservationService;
    
    
    // Items 엔티티를 ID로 조회하는 메서드
    public Items findById(Integer itemId) {
        return itemsRepository.findById(itemId).orElse(null);  // Optional을 사용하여 null 처리
    }
    
    // 구역 세부 정보를 업데이트하는 메서드
    @Transactional
    public void updateZoneDetails(Integer itemId, BigDecimal newPrice) {
        log.info("updateZoneDetails for itemId: {}, newPrice: {}", itemId, newPrice);
        Items item = findById(itemId);
        
        // 변경 사항과 상관없이 항상 히스토리 업데이트
        updateItemHistory(item, newPrice.intValue());

        // 아이템 세부 정보 업데이트
        item.setItemPrice(newPrice.intValue());
        
        itemsRepository.save(item);
        log.info("Zone details updated: {}", item);
    }

    // 아이템 세부 정보를 업데이트하는 메서드
    @Transactional
    public void updateItemDetails(Integer itemId, BigDecimal newPrice, String newDesc) {
        log.info("updateItemDetails for itemId: {}, newPrice: {}, newDesc: {}", itemId, newPrice, newDesc);
        Items item = findById(itemId);
        
        // 변경 사항과 상관없이 항상 히스토리 업데이트
        updateItemHistory(item, newPrice.intValue());

        // 아이템 세부 정보 업데이트
        item.setItemPrice(newPrice.intValue());
        item.setItemDesc(newDesc);
       
        
        itemsRepository.save(item);
        log.info("Item details updated: {}", item);
    }


    @Transactional
    private void updateItemHistory(Items item, int newPrice) {
        log.info("updateItemHistory for itemId: {}, newPrice: {}", item.getItemId(), newPrice);
        
        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
        if (currentHistory != null) {
            currentHistory.setEndDate(LocalDateTime.now());
            currentHistory.setSpecial(0); // 기존 히스토리 종료 시 special을 0으로 설정
            itemsHistoryRepository.save(currentHistory);
            log.info("Existing history updated: {}", currentHistory);
        }

        ItemsHistory newHistory = new ItemsHistory();
        newHistory.setItems(item);
        newHistory.setItemPrice(newPrice);
        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1)); // 새로운 시작 시간 설정
        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59)); // 무기한 종료 시간 설정

       
        
        itemsHistoryRepository.save(newHistory);
        log.info("New history inserted: {}", newHistory);
    }
  
    
    @Transactional
    public List<Integer> getLatestPricesWithSpecialZero() {
        return itemsHistoryRepository.findLatestPricesWithSpecialZero();
    }
    
    
    // 모든 아이템에 대해 최신 가격을 가져오는 메서드
    public List<Items> getAllItemsWithLatestPrice() {
        List<Items> items = reservationService.getAllItems(); // 모든 아이템을 가져옴

        // 최신 가격을 가져옴
        List<Integer> latestPricesWithSpecialZero = itemsHistoryRepository.findLatestPricesWithSpecialZero();

        log.info("Latest prices with special zero: {}", latestPricesWithSpecialZero);

        // 최신 가격을 items 리스트에 매핑
        int startIndexForItems = 21 - 1; // 아이템 ID가 21부터 시작하므로 인덱스는 20부터
        for (int i = 0; i < items.size(); i++) {
            int priceIndex = startIndexForItems + i;
            if (priceIndex < latestPricesWithSpecialZero.size()) {
                items.get(i).setItemPrice(latestPricesWithSpecialZero.get(priceIndex)); // 최신 가격을 설정
                log.info("Item ID: {}, Latest Price Set: {}", items.get(i).getItemId(), latestPricesWithSpecialZero.get(priceIndex));
            }
        }

        return items;
    }

    // 모든 구역에 대해 최신 가격을 가져오는 메서드
    public List<Items> getAllZonesWithLatestPrice() {
        List<Items> zones = reservationService.getAllZones(); // 모든 구역을 가져옴

        // 최신 가격을 가져옴
        List<Integer> latestPricesWithSpecialZero = itemsHistoryRepository.findLatestPricesWithSpecialZero();

        log.info("Latest prices with special zero: {}", latestPricesWithSpecialZero);

        // 최신 가격을 zones 리스트에 매핑
        for (int i = 0; i < zones.size(); i++) {
            if (i < latestPricesWithSpecialZero.size()) {
                zones.get(i).setItemPrice(latestPricesWithSpecialZero.get(i)); // 최신 가격을 설정
                log.info("Zone ID: {}, Latest Price Set: {}", zones.get(i).getItemId(), latestPricesWithSpecialZero.get(i));
            }
        }

        return zones;
    }
    
}