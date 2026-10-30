package com.demo.fintrack.Repository;

import com.demo.fintrack.Domain.Entity.Transaction;
import com.demo.fintrack.Domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction , UUID> {

    Optional<Transaction> findByIdAndUser_Id(UUID id, UUID userId);

    List<Transaction> findByUser_Id(UUID userId);

    List<Transaction> findByUser_IdAndType(UUID id, TransactionType type);

    @Query(value = "Select COALESCE(sum(amount), 0) from Transaction t where t.user.id = :userId and t.category.id = :categoryId and t.type = com.demo.fintrack.Domain.TransactionType.EXPENSE and t.date between :startDate and :endDate")
    BigDecimal totalAmountSpentByCategoryAndDate(@Param("userId") UUID userId,@Param("categoryId") UUID categoryId,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
}
