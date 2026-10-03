package com.flower.shop.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户订单（主表）：下单时按花束售价×数量汇总出 total_amount。
 */
@Entity
@Table(name = "customer_order")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 64)
    public String customer;

    @Column(nullable = false, length = 24)
    public String phone;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    public BigDecimal totalAmount;

    /** 待付款 / 已付款 / 已取消 */
    @Column(nullable = false, length = 16)
    public String status;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<OrderItem> items = new ArrayList<>();
}
