package com.flower.shop.service;

import com.flower.shop.common.BizException;
import com.flower.shop.common.Dict;
import com.flower.shop.entity.Delivery;
import com.flower.shop.repository.CustomerOrderRepository;
import com.flower.shop.repository.DeliveryRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 今日配送业务：关联订单必须存在、同一订单只允许一条未结案配送单；
 * 仅配送中可标记完成、仅待配送可取消。
 */
@Service
public class DeliveryService {

    private final DeliveryRepository repo;
    private final CustomerOrderRepository orderRepo;

    public DeliveryService(DeliveryRepository repo, CustomerOrderRepository orderRepo) {
        this.repo = repo;
        this.orderRepo = orderRepo;
    }

    public List<Delivery> list(String status) {
        if (status == null || status.isBlank()) {
            return repo.findAll();
        }
        return repo.findByStatus(status);
    }

    @Transactional
    public Delivery create(Delivery input) {
        if (input.orderId == null) {
            throw new BizException("配送单必须关联订单");
        }
        if (!orderRepo.existsById(input.orderId)) {
            throw new BizException("关联的订单不存在");
        }
        if (input.address == null || input.address.isBlank()) {
            throw new BizException("配送地址不能为空");
        }
        if (input.expectSlot == null || input.expectSlot.isBlank()) {
            throw new BizException("期望配送时段不能为空");
        }
        if (!repo.findByOrderIdAndStatusIn(input.orderId, Dict.DELIVERY_ACTIVE).isEmpty()) {
            throw new BizException("该订单已存在未完成的配送单");
        }
        Delivery d = new Delivery();
        d.orderId = input.orderId;
        d.address = input.address;
        d.expectSlot = input.expectSlot;
        d.status = "待配送";
        d.createdAt = LocalDateTime.now();
        return repo.save(d);
    }

    @Transactional
    public Delivery update(Long id, Delivery input) {
        Delivery d = repo.findById(id).orElseThrow(() -> new BizException("配送单不存在"));
        if (input.address != null) {
            if (input.address.isBlank()) {
                throw new BizException("配送地址不能为空");
            }
            d.address = input.address;
        }
        if (input.expectSlot != null) {
            if (input.expectSlot.isBlank()) {
                throw new BizException("期望配送时段不能为空");
            }
            d.expectSlot = input.expectSlot;
        }
        return repo.save(d);
    }

    @Transactional
    public Delivery finish(Long id) {
        Delivery d = repo.findById(id).orElseThrow(() -> new BizException("配送单不存在"));
        if (!"配送中".equals(d.status)) {
            throw new BizException("只有配送中的配送单可标记完成");
        }
        d.status = "已完成";
        return repo.save(d);
    }

    @Transactional
    public Delivery cancel(Long id) {
        Delivery d = repo.findById(id).orElseThrow(() -> new BizException("配送单不存在"));
        if (!"待配送".equals(d.status)) {
            throw new BizException("只有待配送的配送单可取消");
        }
        d.status = "已取消";
        return repo.save(d);
    }
}
