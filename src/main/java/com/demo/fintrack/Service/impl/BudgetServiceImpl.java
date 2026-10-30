package com.demo.fintrack.Service.impl;

import com.demo.fintrack.Domain.CategoryType;
import com.demo.fintrack.Domain.DTO.BudgetPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetResponseDTO;
import com.demo.fintrack.Domain.Entity.Budget;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Mapper.BudgetMapper;
import com.demo.fintrack.Repository.BudgetRepository;
import com.demo.fintrack.Repository.CategoryRepository;
import com.demo.fintrack.Repository.TransactionRepository;
import com.demo.fintrack.Repository.UserRepository;
import com.demo.fintrack.Service.BudgetCalculation;
import com.demo.fintrack.Service.BudgetService;
import com.demo.fintrack.Service.Exception.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;

    private final UUID userId = UUID.fromString("464e185b-05c4-4d68-9b4c-2945b13c2703");

    public BudgetServiceImpl(BudgetRepository budgetRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository, UserRepository userRepository, BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.budgetMapper = budgetMapper;
    }

//    create

    @Override
    public BudgetResponseDTO createBudget(BudgetRequestDTO requestDTO) {

        if(budgetRepository.existsByUser_IdAndCategory_IdAndMonth(userId, requestDTO.getCategoryId(), requestDTO.getMonth())){
            throw new BudgetAlreadyExistException("Budget Already Exist");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Category category = categoryRepository.findById(requestDTO.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        if(category.getType() != CategoryType.EXPENSE){
            throw new InvalidCategoryTypeException("Budget should have only expense type");
        }

        if( category.getUser() != null && !category.getUser().getId().equals(userId)){
            throw new CategoryNotBelongToUserException("This category does not belong to the User");
        }

        Budget budget = budgetMapper.toEntity(requestDTO);
        budget.setUser(user);
        budget.setCategory(category);
        Budget savedBudget =  budgetRepository.save(budget);

        LocalDate startDate = savedBudget.getMonth().atDay(1);
        LocalDate endDate = savedBudget.getMonth().atEndOfMonth();

        BigDecimal amountSpent = transactionRepository.totalAmountSpentByCategoryAndDate(savedBudget.getUser().getId(), savedBudget.getCategory().getId(), startDate, endDate);

        BudgetCalculation budgetCalculation = calculate(savedBudget.getAmount(),amountSpent);

        return budgetMapper.toBudgetResponseDTO(savedBudget,amountSpent, budgetCalculation.getAmountLeft(), budgetCalculation.getSpentPercentage(), budgetCalculation.isExceeded(), budgetCalculation.getExceededPercentage());
    }


//    get

    @Override
    public BudgetResponseDTO getBudgetByID(UUID budgetId) {

        Budget budget = budgetRepository.findByIdAndUser_Id(budgetId, userId).orElseThrow(() -> new BudgetNotFoundException("Budget Not found"));
        LocalDate startDate = budget.getMonth().atDay(1);
        LocalDate endDate = budget.getMonth().atEndOfMonth();

        BigDecimal amountSpent = transactionRepository.totalAmountSpentByCategoryAndDate(budget.getUser().getId(), budget.getCategory().getId(), startDate, endDate);

        BudgetCalculation budgetCalculation = calculate(budget.getAmount(),amountSpent);

        return budgetMapper.toBudgetResponseDTO(budget,amountSpent, budgetCalculation.getAmountLeft(), budgetCalculation.getSpentPercentage(), budgetCalculation.isExceeded(), budgetCalculation.getExceededPercentage());
    }

    @Override
    public List<BudgetResponseDTO> getBudgetByUserId() {
        return budgetRepository.findByUser_Id(userId).stream().map(existingBudget -> {
            LocalDate startDate = existingBudget.getMonth().atDay(1);
            LocalDate endDate = existingBudget.getMonth().atEndOfMonth();

            BigDecimal amountSpent = transactionRepository.totalAmountSpentByCategoryAndDate(existingBudget.getUser().getId(), existingBudget.getCategory().getId(), startDate, endDate);

            BudgetCalculation budgetCalculation = calculate(existingBudget.getAmount(),amountSpent);

            return budgetMapper.toBudgetResponseDTO(existingBudget,amountSpent, budgetCalculation.getAmountLeft(), budgetCalculation.getSpentPercentage(), budgetCalculation.isExceeded(), budgetCalculation.getExceededPercentage());
        }).toList();

    }

    @Override
    public List<BudgetResponseDTO> getBudgetByMonthAndUserId(YearMonth month) {
        return budgetRepository.findByMonthAndUser_Id(month, userId).stream().map(existingBudget -> {
            LocalDate startDate = existingBudget.getMonth().atDay(1);
            LocalDate endDate = existingBudget.getMonth().atEndOfMonth();

            BigDecimal amountSpent = transactionRepository.totalAmountSpentByCategoryAndDate(existingBudget.getUser().getId(), existingBudget.getCategory().getId(), startDate, endDate);

            BudgetCalculation budgetCalculation = calculate(existingBudget.getAmount(),amountSpent);

            return budgetMapper.toBudgetResponseDTO(existingBudget,amountSpent, budgetCalculation.getAmountLeft(), budgetCalculation.getSpentPercentage(), budgetCalculation.isExceeded(), budgetCalculation.getExceededPercentage());
        }).toList();
    }

    @Override
    public List<BudgetResponseDTO> getBudgetByCategory(UUID categoryId) {
        return budgetRepository.findByUser_IdAndCategory_Id(userId, categoryId).stream().map(existingBudget -> {
            LocalDate startDate = existingBudget.getMonth().atDay(1);
            LocalDate endDate = existingBudget.getMonth().atEndOfMonth();

            BigDecimal amountSpent = transactionRepository.totalAmountSpentByCategoryAndDate(existingBudget.getUser().getId(), existingBudget.getCategory().getId(), startDate, endDate);

            BudgetCalculation budgetCalculation = calculate(existingBudget.getAmount(),amountSpent);

            return budgetMapper.toBudgetResponseDTO(existingBudget,amountSpent, budgetCalculation.getAmountLeft(), budgetCalculation.getSpentPercentage(), budgetCalculation.isExceeded(), budgetCalculation.getExceededPercentage());
        }).toList();
    }


//    Update

    @Override
    public BudgetResponseDTO updateBudget(BudgetPatchRequestDTO requestDTO, UUID budgetId) {

        Budget budget = budgetRepository.findByIdAndUser_Id(budgetId,userId).map(existingBudget -> {
            Optional.ofNullable(requestDTO.getAmount()).ifPresent(existingBudget::setAmount);
            Optional.ofNullable(requestDTO.getMonth()).ifPresent(existingBudget::setMonth);

            if(requestDTO.getCategoryId() != null){
                Category category = categoryRepository.findById(requestDTO.getCategoryId()).orElseThrow( () -> new CategoryNotFoundException("Category not found"));
                if(category.getType() != CategoryType.EXPENSE){
                    throw new InvalidCategoryTypeException("Budget should have only expense type");
                }
                if( category.getUser() != null && !category.getUser().getId().equals(userId)){
                    throw new CategoryNotBelongToUserException("This category does not belong to the User");
                }

                existingBudget.setCategory(category);
            }

            if(budgetRepository.existsByUser_IdAndCategory_IdAndMonthAndIdNot(userId, existingBudget.getCategory().getId(), existingBudget.getMonth(), existingBudget.getId())){
                throw new BudgetAlreadyExistException("This budget already exists");
            }

            return budgetRepository.save(existingBudget);

        }).orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        LocalDate startDate = budget.getMonth().atDay(1);
        LocalDate endDate = budget.getMonth().atEndOfMonth();

        BigDecimal amountSpent = transactionRepository.totalAmountSpentByCategoryAndDate(budget.getUser().getId(), budget.getCategory().getId(), startDate, endDate);

        BudgetCalculation budgetCalculation = calculate(budget.getAmount(),amountSpent);

        return budgetMapper.toBudgetResponseDTO(budget,amountSpent, budgetCalculation.getAmountLeft(), budgetCalculation.getSpentPercentage(), budgetCalculation.isExceeded(), budgetCalculation.getExceededPercentage());
    }

//    Delete

    @Override
    public void deleteBudget(UUID budgetId) {
        Budget budget = budgetRepository.findByIdAndUser_Id(budgetId, userId).orElseThrow(() -> new BudgetNotFoundException("Budget not found"));
        budgetRepository.delete(budget);
    }

//    Calculation

    private BudgetCalculation calculate(BigDecimal totalAmount, BigDecimal amountSpent){
        BudgetCalculation result = new BudgetCalculation();

        BigDecimal amountLeft = totalAmount.subtract(amountSpent);
        int spentPercentage = amountSpent.multiply(BigDecimal.valueOf(100)).divide(totalAmount, 0, RoundingMode.HALF_UP).intValue();
        boolean isExceeded = amountSpent.compareTo(totalAmount) > 0;
        int exceededPercentage = 0;

        if(isExceeded){
            exceededPercentage = amountSpent.subtract(totalAmount).multiply(BigDecimal.valueOf(100)).divide(totalAmount, 0, RoundingMode.HALF_UP).intValue();
            amountLeft = BigDecimal.valueOf(0);
        }

        result.setAmountLeft(amountLeft);
        result.setSpentPercentage(spentPercentage);
        result.setExceeded(isExceeded);
        result.setExceededPercentage(exceededPercentage);
        return result;
    }

}
