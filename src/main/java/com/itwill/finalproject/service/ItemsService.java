package com.itwill.finalproject.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class ItemsService {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private ItemsHistoryRepository itemsHistoryRepository;
    
    // Items 엔티티를 ID로 조회하는 메서드
    public Items findById(Integer itemId) {
        return itemsRepository.findById(itemId).orElse(null);  // Optional을 사용하여 null 처리
    }
    
 // Zones 업데이트 전용 메서드
    @Transactional
    public void updateZoneDetails(Integer itemId, BigDecimal newPrice, String newCheck) {
    	log.info("updateZoneDetails");
        Items item = findById(itemId);
        if (item != null) {
            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());

            if (priceChanged) {
            	updateItemHistory(item, newPrice.intValue(),newCheck);
            	item.setItemPrice(newPrice.intValue());
            	if ("on".equals(newCheck)) {
                	item.setSpecial(1);
                } else {
                	item.setSpecial(0);
                }
                
                itemsRepository.save(item);
            }
        }
    }

    // Items 업데이트 메서드 (기존에 있던 메서드)
    @Transactional
    public void updateItemDetails(Integer itemId, BigDecimal newPrice, String newDesc, String newCheck) {
    	log.info("updateItemDetails");
        Items item = findById(itemId);
        if (item != null) {
            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
            boolean descChanged = (newDesc != null && !newDesc.equals(item.getItemDesc()));

            if (priceChanged) {
            	log.info("history insert");
                updateItemHistory(item, newPrice.intValue(),newCheck);
                item.setItemPrice(newPrice.intValue());
                if ("on".equals(newCheck)) {
                	item.setSpecial(1);
                } else {
                	item.setSpecial(0);
                }
                log.info("item에 price update");
                itemsRepository.save(item);
            }
            if (descChanged) { //설명만 변경시
                log.info("item에 desc update");
            	item.setItemDesc(newDesc);
                itemsRepository.save(item);
            }
        }
    }

    // History 업데이트 메서드 (공통 사용)
    @Transactional
    private void updateItemHistory(Items item, int newPrice, String newCheck) {
    	log.info("updateItemHistory");
        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
        if (currentHistory != null) {
//        	if ("on".equals(newCheck)) {
//        		currentHistory.setSpecial(0);
//        	}
            currentHistory.setEndDate(LocalDateTime.now());
            itemsHistoryRepository.save(currentHistory);
        }

        ItemsHistory newHistory = new ItemsHistory();
        newHistory.setItems(item);
        newHistory.setItemPrice(newPrice);
        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        
        if("on".equals(newCheck)) {
        	newHistory.setSpecial(1);
        }
        itemsHistoryRepository.save(newHistory);
    }

}