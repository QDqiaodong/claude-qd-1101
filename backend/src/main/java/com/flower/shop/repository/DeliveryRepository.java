package com.flower.shop.repository;

import com.flower.shop.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByStatus(String status);

    List<Delivery> findByOrderIdAndStatusIn(Long orderId, List<String> statuses);
}
