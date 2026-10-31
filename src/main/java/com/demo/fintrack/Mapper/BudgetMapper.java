package com.demo.fintrack.Mapper;

import com.demo.fintrack.Domain.DTO.BudgetDTOs.BudgetRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetDTOs.BudgetResponseDTO;
import com.demo.fintrack.Domain.Entity.Budget;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BudgetMapper {

    public Budget toEntity(BudgetRequestDTO requestDTO){
        Budget budget = new Budget();
        budget.setAmount(requestDTO.getAmount());
        budget.setMonth(requestDTO.getMonth());
        return budget;
    }

    public BudgetResponseDTO toBudgetResponseDTO(Budget budget, BigDecimal amountSpent, BigDecimal amountLeft, int spentPercentage, boolean isExceeded, int exceededPercentage ){
        BudgetResponseDTO responseDTO = new BudgetResponseDTO();
        responseDTO.setBudgetId(budget.getId());
        responseDTO.setMonth(budget.getMonth());
        responseDTO.setTotalAmount(budget.getAmount());
        responseDTO.setCategoryName(budget.getCategory().getName());

        responseDTO.setAmountSpend(amountSpent);
        responseDTO.setAmountLeft(amountLeft);
        responseDTO.setSpentPercentage(spentPercentage);
        responseDTO.setExceeded(isExceeded);
        responseDTO.setExceededPercentage(exceededPercentage);

        return responseDTO;
    }



}
