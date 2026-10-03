package com.flower.shop.service;

import com.flower.shop.common.BizException;
import com.flower.shop.common.Dict;
import com.flower.shop.entity.FlowerMaterial;
import com.flower.shop.repository.BouquetItemRepository;
import com.flower.shop.repository.FlowerMaterialRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 花材台账业务：唯一性、库存非负、状态在字典内、停用前校验未被花束引用。
 */
@Service
public class FlowerMaterialService {

    private final FlowerMaterialRepository repo;
    private final BouquetItemRepository bouquetItemRepository;

    public FlowerMaterialService(FlowerMaterialRepository repo,
                                 BouquetItemRepository bouquetItemRepository) {
        this.repo = repo;
        this.bouquetItemRepository = bouquetItemRepository;
    }

    public List<FlowerMaterial> list(String status) {
        if (status == null || status.isBlank()) {
            return repo.findAll();
        }
        return repo.findByStatus(status);
    }

    @Transactional
    public FlowerMaterial create(FlowerMaterial input) {
        if (input.name == null || input.name.isBlank()) {
            throw new BizException("花材名不能为空");
        }
        if (!Dict.isMaterialCategory(input.category)) {
            throw new BizException("花材类别必须是切花/叶材/辅材");
        }
        if (repo.existsByName(input.name)) {
            throw new BizException("花材名已存在");
        }
        if (input.stock == null || input.stock < 0) {
            throw new BizException("库存不能为负");
        }
        if (input.unitPrice == null || input.unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException("单价不能为负");
        }
        FlowerMaterial m = new FlowerMaterial();
        m.name = input.name;
        m.category = input.category;
        m.unitPrice = input.unitPrice;
        m.stock = input.stock;
        m.status = (input.status == null) ? "在售" : input.status;
        if (!Dict.isMaterialStatus(m.status)) {
            throw new BizException("花材状态必须是在售/停用");
        }
        m.createdAt = LocalDateTime.now();
        return repo.save(m);
    }

    @Transactional
    public FlowerMaterial update(Long id, FlowerMaterial input) {
        FlowerMaterial m = repo.findById(id).orElseThrow(() -> new BizException("花材不存在"));
        if (input.name != null) {
            if (input.name.isBlank()) {
                throw new BizException("花材名不能为空");
            }
            if (repo.existsByNameAndIdNot(input.name, id)) {
                throw new BizException("花材名已存在");
            }
            m.name = input.name;
        }
        if (input.category != null) {
            if (!Dict.isMaterialCategory(input.category)) {
                throw new BizException("花材类别必须是切花/叶材/辅材");
            }
            m.category = input.category;
        }
        if (input.unitPrice != null) {
            if (input.unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException("单价不能为负");
            }
            m.unitPrice = input.unitPrice;
        }
        if (input.stock != null) {
            if (input.stock < 0) {
                throw new BizException("库存不能为负");
            }
            m.stock = input.stock;
        }
        if (input.status != null) {
            if (!Dict.isMaterialStatus(input.status)) {
                throw new BizException("花材状态必须是在售/停用");
            }
            m.status = input.status;
        }
        return repo.save(m);
    }

    @Transactional
    public FlowerMaterial disable(Long id) {
        FlowerMaterial m = repo.findById(id).orElseThrow(() -> new BizException("花材不存在"));
        if ("停用".equals(m.status)) {
            throw new BizException("花材已是停用状态");
        }
        if (bouquetItemRepository.existsByFlowerMaterialId(id)) {
            throw new BizException("该花材正被花束引用，无法停用");
        }
        m.status = "停用";
        return repo.save(m);
    }

    @Transactional
    public FlowerMaterial enable(Long id) {
        FlowerMaterial m = repo.findById(id).orElseThrow(() -> new BizException("花材不存在"));
        m.status = "在售";
        return repo.save(m);
    }
}
