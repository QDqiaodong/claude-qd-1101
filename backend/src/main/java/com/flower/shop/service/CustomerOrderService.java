package com.flower.shop.service;

import com.flower.shop.common.BizException;
import com.flower.shop.entity.Bouquet;
import com.flower.shop.entity.CustomerOrder;
import com.flower.shop.entity.OrderItem;
import com.flower.shop.repository.BouquetRepository;
import com.flower.shop.repository.CustomerOrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户订单业务：手机号格式校验、下单按花束售价汇总金额、仅待付款可取消。
 */
@Service
public class CustomerOrderService {

    private final CustomerOrderRepository repo;
    private final BouquetRepository bouquetRepo;

    private static final Pattern PHONE = Pattern.compile("^1[3-9]\\d{9}$");

    public CustomerOrderService(CustomerOrderRepository repo, BouquetRepository bouquetRepo) {
        this.repo = repo;
        this.bouquetRepo = bouquetRepo;
    }

    public List<CustomerOrder> list(String status) {
        if (status == null || status.isBlank()) {
            return repo.findAll();
        }
        return repo.findByStatus(status);
    }

    @Transactional
    public CustomerOrder create(CustomerOrder input) {
        if (input.customer == null || input.customer.isBlank()) {
            throw new BizException("客户名不能为空");
        }
        if (input.phone == null || !PHONE.matcher(input.phone).matches()) {
            throw new BizException("手机号格式不正确");
        }
        if (input.items == null || input.items.isEmpty()) {
            throw new BizException("订单至少包含一种花束");
        }
        BigDecimal total = BigDecimal.ZERO;
        CustomerOrder o = new CustomerOrder();
        o.customer = input.customer;
        o.phone = input.phone;
        o.status = "待付款";
        o.createdAt = LocalDateTime.now();
        for (OrderItem it : input.items) {
            if (it.qty == null || it.qty <= 0) {
                throw new BizException("花束数量必须大于0");
            }
            Bouquet b = bouquetRepo.findById(it.bouquetId)
                    .orElseThrow(() -> new BizException("引用的花束不存在"));
            OrderItem item = new OrderItem();
            item.bouquetId = it.bouquetId;
            item.qty = it.qty;
            item.order = o;
            o.items.add(item);
            total = total.add(b.price.multiply(BigDecimal.valueOf(it.qty)));
        }
        o.totalAmount = total;
        return repo.save(o);
    }

    @Transactional
    public CustomerOrder update(Long id, CustomerOrder input) {
        CustomerOrder o = repo.findById(id).orElseThrow(() -> new BizException("订单不存在"));
        if (input.customer != null) {
            if (input.customer.isBlank()) {
                throw new BizException("客户名不能为空");
            }
            o.customer = input.customer;
        }
        if (input.phone != null) {
            if (!PHONE.matcher(input.phone).matches()) {
                throw new BizException("手机号格式不正确");
            }
            o.phone = input.phone;
        }
        return repo.save(o);
    }

    @Transactional
    public CustomerOrder cancel(Long id) {
        CustomerOrder o = repo.findById(id).orElseThrow(() -> new BizException("订单不存在"));
        if (!"待付款".equals(o.status)) {
            throw new BizException("只有待付款订单可取消");
        }
        o.status = "已取消";
        return repo.save(o);
    }
}
