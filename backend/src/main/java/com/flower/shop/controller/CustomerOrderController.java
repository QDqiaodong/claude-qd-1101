package com.flower.shop.controller;

import com.flower.shop.common.ApiResponse;
import com.flower.shop.entity.CustomerOrder;
import com.flower.shop.service.CustomerOrderService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer-orders")
public class CustomerOrderController {

    private final CustomerOrderService service;

    public CustomerOrderController(CustomerOrderService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<CustomerOrder>> list(@RequestParam(required = false) String status) {
        return ApiResponse.success(service.list(status));
    }

    @PostMapping
    public ApiResponse<CustomerOrder> create(@RequestBody CustomerOrder body) {
        return ApiResponse.success(service.create(body));
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerOrder> update(@PathVariable Long id, @RequestBody CustomerOrder body) {
        return ApiResponse.success(service.update(id, body));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<CustomerOrder> cancel(@PathVariable Long id) {
        return ApiResponse.success(service.cancel(id));
    }
}
