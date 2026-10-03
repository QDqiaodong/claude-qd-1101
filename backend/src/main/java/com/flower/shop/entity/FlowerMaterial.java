package com.flower.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 花材台账：花店所有可售卖/可使用的花材基础信息。
 */
@Entity
@Table(name = "flower_material", uniqueConstraints = @UniqueConstraint(name = "uk_fm_name", columnNames = "name"))
public class FlowerMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 64)
    public String name;

    /** 切花 / 叶材 / 辅材 */
    @Column(nullable = false, length = 16)
    public String category;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    public BigDecimal unitPrice;

    @Column(nullable = false)
    public Integer stock;

    /** 在售 / 停用 */
    @Column(nullable = false, length = 16)
    public String status;

    @Column(name = "created_at")
    public LocalDateTime createdAt;
}
