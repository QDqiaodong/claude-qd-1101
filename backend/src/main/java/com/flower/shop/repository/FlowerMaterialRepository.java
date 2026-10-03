package com.flower.shop.repository;

import com.flower.shop.entity.FlowerMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FlowerMaterialRepository extends JpaRepository<FlowerMaterial, Long> {

    List<FlowerMaterial> findByStatus(String status);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
