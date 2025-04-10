package com.ecom.service;

import com.ecom.dtos.requests.AddressRequest;
import com.ecom.model.Address;

public interface AddressService {
    Address getAddressByUserId(Integer userId);

    Address updateAddress(Address address);

    void createAddress(AddressRequest address);

    void deleteAddress(int id);

    void deleteAddressByUserId(int userId);
}
