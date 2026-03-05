package com.growthhub.salon.repository;
import com.growthhub.salon.entity.InventoryItem;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {
    Optional<InventoryItem> findBySku(String sku);
    List<InventoryItem> findByStatus(String status);
    List<InventoryItem> findByCategory(String category);
    @Query("SELECT i FROM InventoryItem i WHERE i.stock <= i.minStock")
    List<InventoryItem> findLowStock();
}
