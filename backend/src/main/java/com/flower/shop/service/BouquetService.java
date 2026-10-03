package com.flower.shop.service;

import com.flower.shop.common.BizException;
import com.flower.shop.entity.Bouquet;
import com.flower.shop.entity.BouquetItem;
import com.flower.shop.entity.FlowerMaterial;
import com.flower.shop.repository.BouquetRepository;
import com.flower.shop.repository.FlowerMaterialRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 花束配方业务：花束名唯一、售价>0、至少一种花材；
 * 明细引用的花材必须存在且为在售，否则拦截。
 */
@Service
public class BouquetService {

    private final BouquetRepository repo;
    private final FlowerMaterialRepository materialRepo;

    public BouquetService(BouquetRepository repo, FlowerMaterialRepository materialRepo) {
        this.repo = repo;
        this.materialRepo = materialRepo;
    }

    public List<Bouquet> list() {
        return repo.findAll();
    }

    @Transactional
    public Bouquet create(Bouquet input) {
        if (input.name == null || input.name.isBlank()) {
            throw new BizException("花束名不能为空");
        }
        if (repo.existsByName(input.name)) {
            throw new BizException("花束名已存在");
        }
        if (input.price == null || input.price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("售价必须大于0");
        }
        if (input.items == null || input.items.isEmpty()) {
            throw new BizException("花束至少包含一种花材");
        }
        Bouquet b = new Bouquet();
        b.name = input.name;
        b.price = input.price;
        b.createdAt = LocalDateTime.now();
        for (BouquetItem it : input.items) {
            if (it.qty == null || it.qty <= 0) {
                throw new BizException("花材数量必须大于0");
            }
            FlowerMaterial fm = materialRepo.findById(it.flowerMaterialId)
                    .orElseThrow(() -> new BizException("引用的花材不存在"));
            if (!"在售".equals(fm.status)) {
                throw new BizException("引用的花材已停用：" + fm.name);
            }
            BouquetItem item = new BouquetItem();
            item.flowerMaterialId = it.flowerMaterialId;
            item.qty = it.qty;
            item.bouquet = b;
            b.items.add(item);
        }
        return repo.save(b);
    }

    @Transactional
    public Bouquet update(Long id, Bouquet input) {
        Bouquet b = repo.findById(id).orElseThrow(() -> new BizException("花束不存在"));
        if (input.name != null) {
            if (input.name.isBlank()) {
                throw new BizException("花束名不能为空");
            }
            if (repo.existsByNameAndIdNot(input.name, id)) {
                throw new BizException("花束名已存在");
            }
            b.name = input.name;
        }
        if (input.price != null) {
            if (input.price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException("售价必须大于0");
            }
            b.price = input.price;
        }
        return repo.save(b);
    }
}
