package com.shopverse.mapper;

import com.shopverse.dto.response.AddressResponse;
import com.shopverse.entity.Address;

public final class AddressMapper {

    private AddressMapper() {}

    public static AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .build();
    }
}
