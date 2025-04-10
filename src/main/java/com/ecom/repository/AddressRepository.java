package com.ecom.repository;

import com.ecom.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    Address getFirstByUserId(Integer userId);

    @Modifying
    void deleteByUserId(Integer userId);
}
