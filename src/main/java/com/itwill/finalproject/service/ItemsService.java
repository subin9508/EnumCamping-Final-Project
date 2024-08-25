package com.itwill.finalproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.itwill.finalproject.domain.Items;
import com.itwill.finalproject.domain.ItemsHistory;
import com.itwill.finalproject.repository.ItemsRepository;
import com.itwill.finalproject.repository.ItemsHistoryRepository;

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
    
//    public void updateItemDetails(Integer itemId, BigDecimal newPrice, String newDesc) {
//        Items item = findById(itemId);
//        if (item != null) {
//            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
//            boolean descChanged = !item.getItemDesc().equals(newDesc);
//
//            if (priceChanged || descChanged) {
//                updateItemHistory(item, newPrice.intValue());
//                item.setItemPrice(newPrice.intValue());
//                item.setItemDesc(newDesc);
//                itemsRepository.save(item);
//            }
//        }
//    }
//
//    private void updateItemHistory(Items item, int newPrice) {
//        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
//        if (currentHistory != null) {
//            currentHistory.setEndDate(LocalDateTime.now());
//            itemsHistoryRepository.save(currentHistory);
//        }
//
//        ItemsHistory newHistory = new ItemsHistory();
//        newHistory.setItems(item);
//        newHistory.setItemPrice(newPrice);
//        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
//        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
//        itemsHistoryRepository.save(newHistory);
//    }
    
//    public void updateItemDetails(Integer itemId, BigDecimal newPrice, String newDesc) {
//        Items item = findById(itemId);
//        if (item != null) {
//            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
//            boolean descChanged = !item.getItemDesc().equals(newDesc);
//
//            if (priceChanged || descChanged) {
//                if (priceChanged) {
//                    updateItemHistory(item, "price", newPrice.intValue());
//                }
//                if (descChanged) {
//                    updateItemHistory(item, "description", newDesc);
//                }
//
//                item.setItemPrice(newPrice.intValue());
//                item.setItemDesc(newDesc);
//                itemsRepository.save(item);
//            }
//        }
//    }
//
//    private void updateItemHistory(Items item, String fieldChanged, Object newValue) {
//        ItemsHistory lastHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
//        if (lastHistory != null) {
//            lastHistory.setEndDate(LocalDateTime.now());
//            itemsHistoryRepository.save(lastHistory);
//        }
//
//        ItemsHistory newHistory = new ItemsHistory();
//        newHistory.setItems(item);
//        if (fieldChanged.equals("price")) {
//            newHistory.setItemPrice((Integer) newValue);
//        } 
//        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
//        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
//        itemsHistoryRepository.save(newHistory);
//    }
    
 // Zones 업데이트 전용 메서드
    @Transactional
    public void updateZoneDetails(Integer itemId, BigDecimal newPrice) {
        Items item = findById(itemId);
        if (item != null) {
            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());

            if (priceChanged) {
                updateItemHistory(item, newPrice.intValue());
                item.setItemPrice(newPrice.intValue());
                itemsRepository.save(item);
            }
        }
    }

    // Items 업데이트 메서드 (기존에 있던 메서드)
    @Transactional
    public void updateItemDetails(Integer itemId, BigDecimal newPrice, String newDesc) {
        Items item = findById(itemId);
        if (item != null) {
            boolean priceChanged = !item.getItemPrice().equals(newPrice.intValue());
            boolean descChanged = (newDesc != null && !newDesc.equals(item.getItemDesc()));

            if (priceChanged || descChanged) {
                updateItemHistory(item, newPrice.intValue());
                item.setItemPrice(newPrice.intValue());
                item.setItemDesc(newDesc);
                itemsRepository.save(item);
            }
        }
    }

    // History 업데이트 메서드 (공통 사용)
    @Transactional
    private void updateItemHistory(Items item, int newPrice) {
        ItemsHistory currentHistory = itemsHistoryRepository.findTopByItemsOrderByStartDateDesc(item);
        if (currentHistory != null) {
            currentHistory.setEndDate(LocalDateTime.now());
            itemsHistoryRepository.save(currentHistory);
        }

        ItemsHistory newHistory = new ItemsHistory();
        newHistory.setItems(item);
        newHistory.setItemPrice(newPrice);
        newHistory.setStartDate(LocalDateTime.now().plusSeconds(1));
        newHistory.setEndDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        itemsHistoryRepository.save(newHistory);
    }

}