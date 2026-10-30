package com.demo.fintrack.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BudgetCalculation {

    private BigDecimal amountLeft;

    private int spentPercentage;

    private boolean isExceeded;

    private int exceededPercentage;

}
