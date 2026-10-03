package com.flower.shop.controller;

import com.flower.shop.common.ApiResponse;
import com.flower.shop.entity.Bouquet;
import com.flower.shop.service.BouquetService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bouquets")
public class BouquetController {

    private final BouquetService service;

    public BouquetController(BouquetService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Bouquet>> list() {
        return ApiResponse.success(service.list());
    }

    @PostMapping
    public ApiResponse<Bouquet> create(@RequestBody Bouquet body) {
        return ApiResponse.success(service.create(body));
    }

    @PutMapping("/{id}")
    public ApiResponse<Bouquet> update(@PathVariable Long id, @RequestBody Bouquet body) {
        return ApiResponse.success(service.update(id, body));
    }
}
