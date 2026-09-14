package com.shopverse.service;

import com.shopverse.dto.request.AddressRequest;
import com.shopverse.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getUserAddresses();
    AddressResponse create(AddressRequest request);
    AddressResponse update(Long id, AddressRequest request);
    void delete(Long id);
    AddressResponse setDefault(Long id);
}
