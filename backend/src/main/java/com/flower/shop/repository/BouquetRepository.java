package com.flower.shop.repository;

import com.flower.shop.entity.Bouquet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BouquetRepository extends JpaRepository<Bouquet, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
