package com.demo.fintrack.Mapper;

import com.demo.fintrack.Domain.DTO.TransactionDTOs.TransactionRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionDTOs.TransactionResponseDTO;
import com.demo.fintrack.Domain.Entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapperImpl {


    public Transaction toEntity(TransactionRequestDTO transactionRequestDTO) {
         Transaction transaction = new Transaction();
         transaction.setAmount(transactionRequestDTO.getAmount());
         transaction.setType(transactionRequestDTO.getType());
         transaction.setDate(transactionRequestDTO.getDate());
         transaction.setDescription(transactionRequestDTO.getDescription());
         return transaction;
    }

    public TransactionResponseDTO toTransactionResponseDTO(Transaction transaction) {
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();

        responseDTO.setId(transaction.getId());
        responseDTO.setAmount(transaction.getAmount());
        responseDTO.setDate(transaction.getDate());
        responseDTO.setCategoryName(transaction.getCategory().getName());
        responseDTO.setType(transaction.getType());
        responseDTO.setDescription(transaction.getDescription());

        return responseDTO;
    }

    public List<TransactionResponseDTO> toTransactionResponseDtoList(List<Transaction> transactions){
        if(transactions == null){
            return null;
        }

        return transactions.stream().map(this::toTransactionResponseDTO).collect(Collectors.toList());
    }

}
