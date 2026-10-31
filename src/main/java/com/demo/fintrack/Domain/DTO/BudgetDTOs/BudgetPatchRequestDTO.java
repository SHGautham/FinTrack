package com.demo.fintrack.Domain.DTO.BudgetDTOs;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BudgetPatchRequestDTO {

    @Positive
    private BigDecimal amount;

    private YearMonth month;

    private UUID categoryId;

}
