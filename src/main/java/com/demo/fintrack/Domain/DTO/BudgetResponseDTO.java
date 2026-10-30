package com.demo.fintrack.Domain.DTO;

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
public class BudgetResponseDTO {

    private UUID budgetId;

    private YearMonth month;

    private BigDecimal totalAmount;

    private String categoryName;

    private BigDecimal amountSpend;

    private BigDecimal amountLeft;

    private int spentPercentage;

    private boolean isExceeded;

    private int exceededPercentage;


//    we will add image here
}
