package com.demo.fintrack.Service;

import com.demo.fintrack.Domain.DTO.BudgetDTOs.BudgetPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetDTOs.BudgetRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetDTOs.BudgetResponseDTO;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface BudgetService {

    BudgetResponseDTO createBudget(BudgetRequestDTO requestDTO);

    BudgetResponseDTO getBudgetByID(UUID budgetId);

    List<BudgetResponseDTO> getBudgetByUserId();

    List<BudgetResponseDTO> getBudgetByMonthAndUserId(YearMonth month);

    List<BudgetResponseDTO> getBudgetByCategory(UUID categoryId);

    BudgetResponseDTO updateBudget(BudgetPatchRequestDTO requestDTO, UUID budgetID);

    void deleteBudget(UUID budgetId);
}
