package com.shopverse.service.impl;

import com.shopverse.dto.request.AddressRequest;
import com.shopverse.dto.response.AddressResponse;
import com.shopverse.entity.Address;
import com.shopverse.entity.User;
import com.shopverse.exception.ResourceNotFoundException;
import com.shopverse.mapper.AddressMapper;
import com.shopverse.repository.AddressRepository;
import com.shopverse.repository.UserRepository;
import com.shopverse.security.SecurityUtils;
import com.shopverse.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses() {
        return addressRepository.findByUserIdOrderByIsDefaultDescIdDesc(SecurityUtils.getCurrentUserId())
                .stream().map(AddressMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public AddressResponse create(AddressRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (Boolean.TRUE.equals(request.getIsDefault()) || addressRepository.countByUserId(userId) == 0) {
            clearDefaultAddresses(userId);
            request.setIsDefault(true);
        }

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        return AddressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse update(Long id, AddressRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Address address = findUserAddress(id, userId);

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setCountry(request.getCountry() != null ? request.getCountry() : "India");

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultAddresses(userId);
            address.setIsDefault(true);
        }

        return AddressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Address address = findUserAddress(id, SecurityUtils.getCurrentUserId());
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefault(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Address address = findUserAddress(id, userId);
        clearDefaultAddresses(userId);
        address.setIsDefault(true);
        return AddressMapper.toResponse(addressRepository.save(address));
    }

    private Address findUserAddress(Long id, Long userId) {
        return addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    private void clearDefaultAddresses(Long userId) {
        addressRepository.findByUserIdOrderByIsDefaultDescIdDesc(userId).forEach(addr -> {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        });
    }
}
