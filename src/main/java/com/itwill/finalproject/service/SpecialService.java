package com.itwill.finalproject.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ItemsHistory;
import com.itwill.finalproject.domain.Special;
import com.itwill.finalproject.dto.ItemPriceDto;
import com.itwill.finalproject.repository.ItemsHistoryRepository;
import com.itwill.finalproject.repository.ItemsRepository;
import com.itwill.finalproject.repository.SpecialRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpecialService {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private ItemsHistoryRepository itemsHistoryRepository;
    
    @Autowired
    private SpecialRepository specialRepository;
    
    // Items 엔티티를 ID로 조회하는 메서드
    public Items findById(Integer itemId) {
        return itemsRepository.findById(itemId).orElse(null);  // Optional을 사용하여 null 처리
    }   
    
    
    // Zones 업데이트 전용 메서드
    @Transactional
    public void updateSpecialZoneDetails(Integer itemId, BigDecimal newPrice, String newCheck) {
        log.info("updateZoneDetails 시작 for itemId: {}, newPrice: {}, newCheck: {}", itemId, newPrice, newCheck);
        Items item = findById(itemId);

        if (item != null) {
            log.info("Item found: itemId = {}, currentPrice = {}, special = {}", item.getItemId(), item.getItemPrice(), item.getSpecial());
            
            // 가격이 변경되었는지 확인
            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
            boolean specialChanged = item.getSpecial() != ("on".equals(newCheck) ? 1 : 0);
            log.info("Price changed: {}, Special changed: {}", priceChanged, specialChanged);
            
         // 가격이 변경되었거나, 특가 상태가 변경된 경우에만 업데이트 진행
            if (priceChanged || specialChanged) {
                log.info("Updating item history and item details...");

                item.setItemPrice(newPrice.intValue());
                item.setSpecial("on".equals(newCheck) ? 1 : 0);
                itemsRepository.save(item);
                
                // 특가 상태가 'on'인 경우만 특가 로그를 추가
                if ("on".equals(newCheck)) {
                    updateSpecialItemHistory(item, newPrice.intValue(), 1);  // '1'은 특가 상태를 나타냄
                    insertIntoSpecialTable(item, newPrice.intValue());
                } else {
                    updateSpecialItemHistory(item, newPrice.intValue(), 0);
                }
            }
        }
    }


    // Items 업데이트 메서드
    @Transactional
    public void updateSpecialItemDetails(Integer itemId, BigDecimal newPrice, String newDesc, String newCheck) {
    	log.info("updateItemDetails 시작 for itemId: {}, newPrice: {}, newDesc: {}, newCheck: {}", itemId, newPrice, newDesc, newCheck);
        Items item = findById(itemId);
        
        if (item != null) {
        	log.info("Item found: itemId = {}, currentPrice = {}, currentDesc = {}, special = {}", item.getItemId(), item.getItemPrice(), item.getItemDesc(), item.getSpecial());
        	
        	// 가격 변경 확인
            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
            boolean specialChanged = item.getSpecial() != ("on".equals(newCheck) ? 1 : 0);
            
            // 설명 변경 확인
            boolean descChanged = (newDesc != null && !newDesc.equals(item.getItemDesc()));
            
            log.info("Price changed: {}, Description changed: {}", priceChanged, descChanged);
            
            // 가격이 변경되었거나, 특가 상태가 변경된 경우에만 업데이트 진행
            if (priceChanged || specialChanged) {
                log.info("Updating item history and item details...");

                item.setItemPrice(newPrice.intValue());
                item.setSpecial("on".equals(newCheck) ? 1 : 0);
                itemsRepository.save(item); //가격이랑 special을 바꿈
                
                // 특가 상태가 'on'인 경우만 특가 로그를 추가
                if ("on".equals(newCheck)) {
                    updateSpecialItemHistory(item, newPrice.intValue(), 1);  // '1'은 특가 상태를 나타냄
                    insertIntoSpecialTable(item, newPrice.intValue());
                } else {
                    updateSpecialItemHistory(item, newPrice.intValue(), 0);
                }
            }

            if (descChanged) { // 설명만 변경 시
                log.info("item에 desc update");
                item.setItemDesc(newDesc);
                itemsRepository.save(item);
                log.info("Item description updated: itemId = {}, newDesc = {}", item.getItemId(), item.getItemDesc());
            }
        }
    }

    
    // itemshistory 테이블에 기록 추가
//    @Transactional
//    private void updateSpecialItemHistory(Items item, int newPrice, int specialStatus) {
//    	log.info("updateItemHistory");
//    	log.info("Updating Item History for itemId={}, newPrice={}, specialStatus={}", item.getItemId(), newPrice, specialStatus);
//    	
//        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
//        if (currentHistory != null) {
//            // 기존의 special = 1 레코드의 end_date만 업데이트하고, special 값을 변경하지 않습니다.
//            currentHistory.setEndDate(LocalDateTime.now());
//            itemsHistoryRepository.save(currentHistory);
//            log.debug("Current history updated: {}", currentHistory);
//        }
//    	
//        ItemsHistory newHistory = new ItemsHistory();
//        newHistory.setItems(item);
//        newHistory.setItemPrice(newPrice);
//        newHistory.setSpecial(specialStatus); // 특가 여부를 반영
//        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
//        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
//        
//        if("1".equals(specialStatus)) {
//        	newHistory.setSpecial(1);
//        }
//        
//        itemsHistoryRepository.save(newHistory);
//        log.debug("New history record added: {}", newHistory);
//    }
    
    
    @Transactional
    private void updateSpecialItemHistory(Items item, int newPrice, int specialStatus) {
        log.info("Updating Item History for itemId={}, newPrice={}, specialStatus={}", item.getItemId(), newPrice, specialStatus);

        // 가장 최근의 ItemsHistory 레코드를 조회합니다.
        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
        if (currentHistory != null) {
            // 기존의 special = 1 레코드의 end_date만 업데이트하고, special 값을 변경하지 않습니다.
            currentHistory.setEndDate(LocalDateTime.now());
            itemsHistoryRepository.save(currentHistory);
        }

        // 새로운 ItemsHistory 레코드를 추가합니다.
        // 중복 삽입을 방지하기 위해 최근 레코드와 상태가 다를 때만 새 레코드를 추가합니다.
            ItemsHistory newHistory = new ItemsHistory();
            newHistory.setItems(item);
            newHistory.setItemPrice(newPrice);
            newHistory.setSpecial(specialStatus);
            newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
            newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
            
            if("1".equals(specialStatus)) {
            	newHistory.setSpecial(1);
            }
            
            itemsHistoryRepository.save(newHistory);
            
            log.debug("New history record added: {}", newHistory);
    }
    
    // 특가 테이블에 데이터 삽입
    @Transactional
    private void insertIntoSpecialTable(Items item, int specialPrice) {
    	log.info("insertIntoSpecialTable 시작 for itemId: {}, specialPrice: {}", item.getItemId(), specialPrice);

        Special special = new Special();
        special.setItems(item);
        special.setItemPrice(specialPrice);
        special.setStartDate(LocalDateTime.now());
        special.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        specialRepository.save(special);
        
        log.info("Inserted into special table for itemId: {}, specialPrice: {}", item.getItemId(), specialPrice);
        log.info("insertIntoSpecialTable 끝 for itemId: {}", item.getItemId());
    }
    
    
    // 특가 여부 해제 시 end_date를 업데이트하고 새로운 레코드를 추가하는 메서드
    @Transactional
    public void updateSpecialEndDateAndInsertRecord(Integer itemId) {
        log.info("특가 여부 해제 시작 itemId={}", itemId);
        
        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        Items item = itemsRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + itemId));
        log.debug("Item 조회 성공: {}", item);
        
        if (item.getSpecial() == 0) {
            log.info("Item is already not special. Skipping end_date update.");
            return;
        }


        // 최근 ItemsHistory의 endDate 업데이트
        ItemsHistory latestHistory = itemsHistoryRepository.findTopByItemsAndSpecialOrderByStartDateDesc(itemId, 1);
        if (latestHistory != null) {
            latestHistory.setEndDate(now.minusSeconds(1));
            //종료날짜업데이트
            itemsHistoryRepository.save(latestHistory);
            log.info("Updated latest history record endDate: {}", latestHistory);
        }
        /*
        List<ItemsHistory> existingRecords = itemsHistoryRepository.findByItemsAndSpecialAndStartDate(item, 0, now);
        if (existingRecords.isEmpty()) {
            ItemsHistory newHistory = new ItemsHistory();
            newHistory.setItems(item);
            newHistory.setItemPrice(item.getItemPrice());
            newHistory.setSpecial(0);
            newHistory.setStartDate(now);
            newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
            itemsHistoryRepository.save(newHistory);
            log.info("ItemsHistory에 새로운 레코드 추가됨: {}", newHistory);
        } else {
            log.info("Already existing special = 0 record for the same start date, not inserting new record.");
        }*/


        // Special 테이블의 end_date 업데이트
        int updatedCount = specialRepository.updateSpecialEndDateWhenSpecialGoesZero(itemId);
        log.info("Special 테이블 업데이트 시도: itemId = {}, endDate = {}, updatedCount = {}", itemId, now, updatedCount);

        if (updatedCount == 0) {
            log.error("Special 테이블 업데이트 실패. 조건을 만족하는 행이 없습니다. itemId: {}", itemId);
        } else {
            log.info("Special 테이블 업데이트 성공. 업데이트된 행의 수: {}", updatedCount);
        }
       }
    
    

    
    // Itmes 에 대한 최신 특가 가격을 Map 형태로 가져오기
    public Map<Integer, Integer> getLatestSpecialPricesForItems() {
        List<ItemPriceDto> specialPricesDto = itemsHistoryRepository.findLatestSpecialPricesDto();
        return specialPricesDto.stream()
                               .collect(Collectors.toMap(ItemPriceDto::getItemId, ItemPriceDto::getItemPrice));
    }

 // Zones에 대한 최신 특가 가격을 Map 형태로 가져오기
    public Map<Integer, Integer> getLatestSpecialPricesForZones() {
        List<ItemPriceDto> specialPricesDto = itemsHistoryRepository.findLatestSpecialPricesDto();
        return specialPricesDto.stream()
                               .collect(Collectors.toMap(ItemPriceDto::getItemId, ItemPriceDto::getItemPrice));
    }
    

    // Items에 대한 최신 정상 가격 가져오기
    public Map<Integer, Integer> getLatestPricesWithSpecialZeroForItems() {
        List<Integer> latestPricesWithSpecialZeroList = itemsHistoryRepository.findLatestPricesWithSpecialZero();
        Map<Integer, Integer> latestPricesWithSpecialZero = new HashMap<>();
        for (int i = 0; i < latestPricesWithSpecialZeroList.size(); i++) {
            if (i >= 20) { // 아이템 ID는 21~32
                latestPricesWithSpecialZero.put(21 + (i - 20), latestPricesWithSpecialZeroList.get(i)); 
            }
        }
        return latestPricesWithSpecialZero;
    }

    // Zones에 대한 최신 정상 가격 가져오기
    public Map<Integer, Integer> getLatestPricesWithSpecialZeroForZones() {
        List<Integer> latestPricesWithSpecialZeroList = itemsHistoryRepository.findLatestPricesWithSpecialZero();
        Map<Integer, Integer> latestPricesWithSpecialZero = new HashMap<>();
        for (int i = 0; i < Math.min(20, latestPricesWithSpecialZeroList.size()); i++) {  // Zone ID는 1~20
            latestPricesWithSpecialZero.put(i + 1, latestPricesWithSpecialZeroList.get(i));
        }
        return latestPricesWithSpecialZero;
    }

}