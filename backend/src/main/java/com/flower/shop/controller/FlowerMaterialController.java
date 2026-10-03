package com.flower.shop.controller;

import com.flower.shop.common.ApiResponse;
import com.flower.shop.entity.FlowerMaterial;
import com.flower.shop.service.FlowerMaterialService;
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
@RequestMapping("/api/flower-materials")
public class FlowerMaterialController {

    private final FlowerMaterialService service;

    public FlowerMaterialController(FlowerMaterialService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<FlowerMaterial>> list(@RequestParam(required = false) String status) {
        return ApiResponse.success(service.list(status));
    }

    @PostMapping
    public ApiResponse<FlowerMaterial> create(@RequestBody FlowerMaterial body) {
        return ApiResponse.success(service.create(body));
    }

    @PutMapping("/{id}")
    public ApiResponse<FlowerMaterial> update(@PathVariable Long id, @RequestBody FlowerMaterial body) {
        return ApiResponse.success(service.update(id, body));
    }

    @PostMapping("/{id}/disable")
    public ApiResponse<FlowerMaterial> disable(@PathVariable Long id) {
        return ApiResponse.success(service.disable(id));
    }

    @PostMapping("/{id}/enable")
    public ApiResponse<FlowerMaterial> enable(@PathVariable Long id) {
        return ApiResponse.success(service.enable(id));
    }
}
