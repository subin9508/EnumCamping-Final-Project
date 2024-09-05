package com.itwill.finalproject.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    
//    // Zones 업데이트 전용 메서드
//    @Transactional
//    public void updateZoneDetails(Integer itemId, BigDecimal newPrice, String newCheck) {
//        log.info("updateZoneDetails");
//        Items item = findById(itemId);
//        if (item != null) {
//            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
//
//            if (priceChanged) {
//                updateItemHistory(item, newPrice.intValue(),newCheck);
//                item.setItemPrice(newPrice.intValue());
//                if ("on".equals(newCheck)) {
//                    item.setSpecial(1);
//                } else {
//                    item.setSpecial(0);
//                }
//                
//                itemsRepository.save(item);
//            }
//        }
//    }
//
//    // Items 업데이트 메서드 (기존에 있던 메서드)
//    @Transactional
//    public void updateItemDetails(Integer itemId, BigDecimal newPrice, String newDesc, String newCheck) {
//        log.info("updateItemDetails");
//        Items item = findById(itemId);
//        if (item != null) {
//            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
//            boolean descChanged = (newDesc != null && !newDesc.equals(item.getItemDesc()));
//
//            if (priceChanged) {
//                log.info("history insert");
//                updateItemHistory(item, newPrice.intValue(),newCheck); // itemshistory에 기록 추가
//                
//                item.setItemPrice(newPrice.intValue());
//                if ("on".equals(newCheck)) {
//                    item.setSpecial(1);
//                } else {
//                    item.setSpecial(0);
//                }
//                log.info("item에 price update");
//                itemsRepository.save(item);
//            }
//            
//            if (descChanged) { //설명만 변경시
//                log.info("item에 desc update");
//                item.setItemDesc(newDesc);
//                itemsRepository.save(item);
//            }
//        }
//    }
//
//    // History 업데이트 메서드 (공통 사용)
//    @Transactional
//    private void updateItemHistory(Items item, int newPrice, String newCheck) {
//        log.info("updateItemHistory");
//        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
//        if (currentHistory != null) {
//            currentHistory.setEndDate(LocalDateTime.now());
//            currentHistory.setSpecial(0); // 기존 기록의 특가 상태 해제
//            itemsHistoryRepository.save(currentHistory);
//        }
//
//        ItemsHistory newHistory = new ItemsHistory();
//        newHistory.setItems(item);
//        newHistory.setItemPrice(newPrice);
//        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
//        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
//        
//        if("on".equals(newCheck)) {
//            newHistory.setSpecial(1);
//        }
//        itemsHistoryRepository.save(newHistory);
//    }
    
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
                itemsRepository.save(item);
                
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
    @Transactional
    private void updateSpecialItemHistory(Items item, int newPrice, int specialStatus) {
    	log.info("updateItemHistory");
    	
        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
        if (currentHistory != null) {
            // 기존의 special = 1 레코드의 end_date만 업데이트하고, special 값을 변경하지 않습니다.
            currentHistory.setEndDate(LocalDateTime.now());
            itemsHistoryRepository.save(currentHistory);
        }
    	
        ItemsHistory newHistory = new ItemsHistory();
        newHistory.setItems(item);
        newHistory.setItemPrice(newPrice);
        newHistory.setSpecial(specialStatus); // 특가 여부를 반영
        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        
        if("1".equals(specialStatus)) {
        	newHistory.setSpecial(1);
        }
        
        itemsHistoryRepository.save(newHistory);
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
    	log.info("특가 여부 해제 itemId={}", itemId);
    	
//    	LocalDateTime endDate = LocalDateTime.now().minusSeconds(1); // 현재 시간에서 1초 전으로 설정
//        log.info("endDate={}", endDate);
//        
//        // itemsHistory 테이블의 특가 레코드의 end_date 업데이트 (special=1인 레코드만 수정)
//        itemsHistoryRepository.updateItemsHistoryEndDateByItemId(itemId, endDate);
        
        // 새로운 레코드 (special = 0) 삽입
        Items item = itemsRepository.findById(itemId)
        		.orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + itemId));

        if (item != null) {        	
            // 새로운 특가 종료 레코드를 추가
            ItemsHistory newHistory = new ItemsHistory();
            newHistory.setItems(item);
            newHistory.setItemPrice(item.getItemPrice());
            newHistory.setSpecial(0); // 특가 해제
            newHistory.setStartDate(LocalDateTime.now());
            newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
            
            itemsHistoryRepository.save(newHistory);
            
            // 방금 삽입된 special = 0 레코드의 start_date 가져오기
            LocalDateTime latestStartDate = newHistory.getStartDate();
            log.info("방금 삽입 latestStartDate={}", latestStartDate);
            
            // special 테이블의 end_date를 최신 start_date의 1초 전으로 설정
            LocalDateTime endDate = latestStartDate.minusSeconds(1);
            log.info("endDate to set for special table = {}", endDate);
            
            // 기본 end date 값
            LocalDateTime defaultEndDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

//            // special 테이블의 end_date 업데이트
//            specialRepository.updateEndDateByItemId(itemId, endDate, defaultEndDate);
        
            // special 테이블의 end_date 업데이트
            int updatedCount = specialRepository.updateEndDateByItemId(itemId, endDate, defaultEndDate);
            log.info("Special table updated rows count: {}", updatedCount);

            if (updatedCount == 0) {
                log.warn("No rows updated in Special table. Check if the itemId and defaultEndDate match the conditions.");
            }
        }
    }
    
        
    
    // Items에 대한 최신 특가 가격 가져오기
//    public List<Integer> getLatestSpecialPricesForItems() {
//        List<Integer> specialPrices = itemsHistoryRepository.findLatestSpecialPrices();
//        if (specialPrices.size() >= 32) {
//            return specialPrices.subList(21, 33);  // Item ID가 21~32인 특가 가격만 가져오기
//        } else {
//            return new ArrayList<>();  // 비어있는 리스트를 반환하여 오류를 방지
//        }
//    }
    
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
    
    // Zones에 대한 최신 특가 가격 가져오기
//    public List<Integer> getLatestSpecialPricesForZones() {
//        List<Integer> specialPrices = itemsHistoryRepository.findLatestSpecialPrices();
//        log.info("specialPrices={}", specialPrices);
//        if (specialPrices.size() >= 20) {
//            return specialPrices.subList(0, 20);  // Zone ID가 1~20인 특가 가격만 가져오기
//        } else {
//            return new ArrayList<>();  // 비어있는 리스트를 반환하여 오류를 방지
//        }
//    }

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