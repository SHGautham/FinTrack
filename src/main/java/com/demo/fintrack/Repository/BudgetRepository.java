package com.demo.fintrack.Repository;

import com.demo.fintrack.Domain.Entity.Budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {


    Optional<Budget> findByIdAndUser_Id(UUID budgetId, UUID userId);

    List<Budget> findByUser_Id(UUID userId);

    List<Budget> findByMonthAndUser_Id(YearMonth month,UUID userId);

    List<Budget> findByUser_IdAndCategory_Id(UUID userId, UUID categoryID);

    boolean existsByUser_IdAndCategory_IdAndMonth(UUID userId, UUID categoryID, YearMonth month);

    boolean existsByUser_IdAndCategory_IdAndMonthAndIdNot(UUID userId, UUID categoryID, YearMonth month, UUID budgetId);

}