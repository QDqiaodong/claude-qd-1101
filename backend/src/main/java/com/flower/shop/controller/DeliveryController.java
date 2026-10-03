package com.flower.shop.controller;

import com.flower.shop.common.ApiResponse;
import com.flower.shop.entity.Delivery;
import com.flower.shop.service.DeliveryService;
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
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Delivery>> list(@RequestParam(required = false) String status) {
        return ApiResponse.success(service.list(status));
    }

    @PostMapping
    public ApiResponse<Delivery> create(@RequestBody Delivery body) {
        return ApiResponse.success(service.create(body));
    }

    @PutMapping("/{id}")
    public ApiResponse<Delivery> update(@PathVariable Long id, @RequestBody Delivery body) {
        return ApiResponse.success(service.update(id, body));
    }

    @PostMapping("/{id}/finish")
    public ApiResponse<Delivery> finish(@PathVariable Long id) {
        return ApiResponse.success(service.finish(id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Delivery> cancel(@PathVariable Long id) {
        return ApiResponse.success(service.cancel(id));
    }
}
