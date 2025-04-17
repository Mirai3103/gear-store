package com.ecom.repository;

import com.ecom.model.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportRepository extends JpaRepository<Import, Integer> {
    @Query("SELECT MONTH(i.importDate), SUM(i.total_money) " +
            "FROM Import i " +
            "WHERE YEAR(i.importDate) = :year " +
            "GROUP BY MONTH(i.importDate)")
    List<Object[]> getMonthlyExpense(@Param("year") int year);


}