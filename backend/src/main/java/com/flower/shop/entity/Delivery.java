package com.flower.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 今日配送：把客户订单安排到具体地址与期望时段，并跟踪配送进度。
 */
@Entity
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "order_id", nullable = false)
    public Long orderId;

    @Column(nullable = false, length = 255)
    public String address;

    /** 期望时段，如 "14:00-16:00" */
    @Column(name = "expect_slot", nullable = false, length = 32)
    public String expectSlot;

    /** 待配送 / 配送中 / 已完成 / 已取消 */
    @Column(nullable = false, length = 16)
    public String status;

    @Column(name = "created_at")
    public LocalDateTime createdAt;
}
