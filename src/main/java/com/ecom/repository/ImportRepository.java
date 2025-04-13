package com.ecom.repository;

import com.ecom.model.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportRepository extends JpaRepository<Import, Integer> {


}