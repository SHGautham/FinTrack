package com.demo.fintrack.Controller;

import com.demo.fintrack.Domain.DTO.BudgetPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetResponseDTO;
import com.demo.fintrack.Service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
public class BudgetController {

    private final BudgetService budgetService;


    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

//    create
    @PostMapping("/createBudget")
    public ResponseEntity<BudgetResponseDTO> createBudget(@Valid @RequestBody BudgetRequestDTO requestDTO){
        return new ResponseEntity<>(budgetService.createBudget(requestDTO), HttpStatus.CREATED);
    }

//    get

    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<BudgetResponseDTO> findBudgetByID(@PathVariable UUID budgetId){
        return new ResponseEntity<>(budgetService.getBudgetByID(budgetId) , HttpStatus.OK);
    }

    @GetMapping("/budget/user")
    public ResponseEntity<List<BudgetResponseDTO>> findBudgetByUser(){
        return new ResponseEntity<>(budgetService.getBudgetByUserId(), HttpStatus.OK);
    }

    @GetMapping("/budget/month/{month}")
    public ResponseEntity<List<BudgetResponseDTO>> findBudgetByMonth(@PathVariable YearMonth month){
        return new ResponseEntity<>(budgetService.getBudgetByMonthAndUserId(month) , HttpStatus.OK);
    }

    @GetMapping("/budget/category/{categoryId}")
    public ResponseEntity<List<BudgetResponseDTO>> findBudgetByCategory(@PathVariable UUID categoryId){
        return new ResponseEntity<>(budgetService.getBudgetByCategory(categoryId) , HttpStatus.OK);
    }


//    update
    @PatchMapping("/updateBudget/{budgetId}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(@Valid @RequestBody BudgetPatchRequestDTO requestDTO, @PathVariable UUID budgetId){
        return new ResponseEntity<>(budgetService.updateBudget(requestDTO,budgetId), HttpStatus.OK);
    }

//    delete

    @DeleteMapping("/deleteBudget/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable UUID budgetId){
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }

}