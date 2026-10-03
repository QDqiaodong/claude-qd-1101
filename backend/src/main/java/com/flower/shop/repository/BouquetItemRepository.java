package com.flower.shop.repository;

import com.flower.shop.entity.BouquetItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BouquetItemRepository extends JpaRepository<BouquetItem, Long> {

    boolean existsByFlowerMaterialId(Long flowerMaterialId);
}
