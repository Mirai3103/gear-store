package com.ecom.repository;

import com.ecom.model.ImportDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportDetailsRepository extends JpaRepository<ImportDetails, Integer> {
}