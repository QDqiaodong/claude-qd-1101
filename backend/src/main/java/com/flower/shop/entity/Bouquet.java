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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 花束配方（主表）：一个花束由若干花材按数量组成。
 */
@Entity
@Table(name = "bouquet", uniqueConstraints = @UniqueConstraint(name = "uk_bq_name", columnNames = "name"))
public class Bouquet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 64)
    public String name;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<BouquetItem> items = new ArrayList<>();
}
