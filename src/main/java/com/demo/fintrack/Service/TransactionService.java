package com.demo.fintrack.Service;

import com.demo.fintrack.Domain.DTO.TransactionPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionResponseDTO;
import com.demo.fintrack.Domain.Entity.Transaction;
import com.demo.fintrack.Domain.TransactionType;
import com.demo.fintrack.Service.Exception.CategoryNotFoundException;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) throws CategoryNotFoundException;

    TransactionResponseDTO getTransactionByID(UUID id);

    List<TransactionResponseDTO> getAllTransaction();

    List<TransactionResponseDTO> getAllTransaction(TransactionType type);

    TransactionResponseDTO updateTransaction(TransactionPatchRequestDTO patchRequestDTO, UUID transactionId);

    void deleteTransaction(UUID id);

}
