package com.ecom.service.impl;

import com.ecom.dtos.requests.AddressRequest;
import com.ecom.model.Address;
import com.ecom.repository.AddressRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public Address getAddressByUserId(Integer userId) {

        return addressRepository.getFirstByUserId(userId);
    }

    @Override
    public Address updateAddress(Address address) {
        var existingAddress = addressRepository.findById(address.getId());
        if (existingAddress.isPresent()) {
            Address addressToUpdate = existingAddress.get();
            addressToUpdate.setFirstName(address.getFirstName());
            addressToUpdate.setLastName(address.getLastName());
            addressToUpdate.setAddress(address.getAddress());
            addressToUpdate.setPostalCode(address.getPostalCode());
            addressToUpdate.setCountry(address.getCountry());
            addressToUpdate.setPhone(address.getPhone());
            addressToUpdate.setEmail(address.getEmail());
            return addressRepository.save(addressToUpdate);
        }
        return null;
    }

    @Transactional
    @Override
    public void createAddress(AddressRequest address) {
        var user = userRepository.findById(address.getUserId());
        if (user.isEmpty()) {
            return;
        }
        var entity = address.toEntity();
        entity.setUser(user.get());
        addressRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteAddress(int id) {
        addressRepository.deleteById(id);
    }

    @Override
    public void deleteAddressByUserId(int userId) {
        var address = addressRepository.getFirstByUserId(userId);
        if (address != null) {
            addressRepository.delete(address);
        }
    }
}
