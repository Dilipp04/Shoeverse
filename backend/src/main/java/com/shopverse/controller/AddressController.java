package com.shopverse.controller;

import com.shopverse.dto.request.AddressRequest;
import com.shopverse.dto.response.AddressResponse;
import com.shopverse.dto.response.ApiResponse;
import com.shopverse.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressResponse>> getAddresses() {
        return ApiResponse.success("Addresses fetched successfully", addressService.getUserAddresses());
    }

    @PostMapping
    public ApiResponse<AddressResponse> create(@Valid @RequestBody AddressRequest request) {
        return ApiResponse.success("Address created successfully", addressService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return ApiResponse.success("Address updated successfully", addressService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ApiResponse.success("Address deleted successfully");
    }

    @PostMapping("/{id}/default")
    public ApiResponse<AddressResponse> setDefault(@PathVariable Long id) {
        return ApiResponse.success("Default address updated", addressService.setDefault(id));
    }
}
