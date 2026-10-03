package com.flower.shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 花束配方明细：花束与花材的多对多关系（含数量）。
 * 通过 bouquet_id 归属花束，flower_material_id 引用花材。
 */
@Entity
@Table(name = "bouquet_item")
public class BouquetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "bouquet_id", nullable = false)
    public Bouquet bouquet;

    @Column(name = "flower_material_id", nullable = false)
    public Long flowerMaterialId;

    @Column(nullable = false)
    public Integer qty;
}
