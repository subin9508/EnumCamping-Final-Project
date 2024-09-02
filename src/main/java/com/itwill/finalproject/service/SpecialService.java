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
    public void updateZoneDetails(Integer itemId, BigDecimal newPrice, String newCheck) {
        log.info("updateZoneDetails");
        Items item = findById(itemId);
        if (item != null) {
            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());

            if (priceChanged) {
                updateItemHistory(item, newPrice.intValue(), newCheck);  // itemshistory에 기록 추가
                item.setItemPrice(newPrice.intValue());
                item.setSpecial("on".equals(newCheck) ? 1 : 0);  // 특가 여부 설정
                itemsRepository.save(item);  // items 테이블에 업데이트

                // 특가 여부가 체크되었을 때, special 테이블에 insert
                if ("on".equals(newCheck)) {
                    insertIntoSpecialTable(item, newPrice.intValue());
                }
            }
        }
    }

    // Items 업데이트 메서드
    @Transactional
    public void updateItemDetails(Integer itemId, BigDecimal newPrice, String newDesc, String newCheck) {
        log.info("updateItemDetails");
        Items item = findById(itemId);
        if (item != null) {
            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
            boolean descChanged = (newDesc != null && !newDesc.equals(item.getItemDesc()));

            if (priceChanged) {
                log.info("history insert");
                updateItemHistory(item, newPrice.intValue(), newCheck); // itemshistory에 기록 추가

                item.setItemPrice(newPrice.intValue());
                item.setSpecial("on".equals(newCheck) ? 1 : 0); // 특가 여부 설정
                itemsRepository.save(item); // items 테이블에 업데이트

                // 특가 여부가 체크되었을 때, special 테이블에 insert
                if ("on".equals(newCheck)) {
                    insertIntoSpecialTable(item, newPrice.intValue());
                }
            }

            if (descChanged) { // 설명만 변경 시
                log.info("item에 desc update");
                item.setItemDesc(newDesc);
                itemsRepository.save(item);
            }
        }
    }

    // itemshistory 테이블에 기록 추가
    @Transactional
    private void updateItemHistory(Items item, int newPrice, String newCheck) {
        log.info("updateItemHistory");
        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
        if (currentHistory != null) {
            currentHistory.setEndDate(LocalDateTime.now());
            currentHistory.setSpecial(0); // 기존 기록의 특가 상태 해제
            itemsHistoryRepository.save(currentHistory);
        }

        ItemsHistory newHistory = new ItemsHistory();
        newHistory.setItems(item);
        newHistory.setItemPrice(newPrice);
        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        newHistory.setSpecial("on".equals(newCheck) ? 1 : 0); // 특가 여부 설정
        itemsHistoryRepository.save(newHistory);
    }
    
    
    
    
    // 특가 테이블에 데이터 삽입
    @Transactional
    private void insertIntoSpecialTable(Items item, int specialPrice) {
        Special special = new Special();
        special.setItems(item);
        special.setItemPrice(specialPrice);
        special.setStartDate(LocalDateTime.now());
        special.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        specialRepository.save(special);
        log.info("Inserted into special table for itemId: {}", item.getItemId());
    }
    
    
    
    
    // Items에 대한 최신 특가 가격 가져오기
    public List<Integer> getLatestSpecialPricesForItems() {
        List<Integer> specialPrices = itemsHistoryRepository.findLatestSpecialPrices();
        if (specialPrices.size() >= 32) {
            return specialPrices.subList(21, 33);  // Item ID가 21~32인 특가 가격만 가져오기
        } else {
            return new ArrayList<>();  // 비어있는 리스트를 반환하여 오류를 방지
        }
    }

    // Zones에 대한 최신 특가 가격 가져오기
    public List<Integer> getLatestSpecialPricesForZones() {
        List<Integer> specialPrices = itemsHistoryRepository.findLatestSpecialPrices();
        if (specialPrices.size() >= 20) {
            return specialPrices.subList(0, 20);  // Zone ID가 1~20인 특가 가격만 가져오기
        } else {
            return new ArrayList<>();  // 비어있는 리스트를 반환하여 오류를 방지
        }
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