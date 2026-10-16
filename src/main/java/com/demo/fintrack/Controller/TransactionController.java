package com.demo.fintrack.Controller;

import com.demo.fintrack.Domain.DTO.TransactionPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionResponseDTO;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Domain.Entity.Transaction;
import com.demo.fintrack.Domain.TransactionType;
import com.demo.fintrack.Mapper.TransactionMapperImpl;
import com.demo.fintrack.Repository.CategoryRepository;
import com.demo.fintrack.Service.Exception.CategoryNotFoundException;
import com.demo.fintrack.Service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

//    Create

    @PostMapping("/createTransaction")
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO){
        return new ResponseEntity<>(transactionService.createTransaction(transactionRequestDTO) , HttpStatus.CREATED);
    }

//    Get

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id){
        return new ResponseEntity<>(transactionService.getTransactionByID(id), HttpStatus.OK);
    }

    @GetMapping("/allTransaction")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransaction(){
        return new ResponseEntity<>(transactionService.getAllTransaction(), HttpStatus.OK);
    }

    @GetMapping("/allTransaction/{type}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionByType(@PathVariable TransactionType type){
        return new ResponseEntity<>(transactionService.getAllTransaction(type), HttpStatus.OK);
    }

//    Update

    @PatchMapping("/updateTransaction/{transactionID}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(@Valid @RequestBody TransactionPatchRequestDTO patchRequestDTO, @PathVariable UUID transactionID){
        return new ResponseEntity<>(transactionService.updateTransaction(patchRequestDTO, transactionID) , HttpStatus.OK);
    }

//    Delete

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id){
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

}
