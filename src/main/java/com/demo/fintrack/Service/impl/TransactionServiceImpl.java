package com.demo.fintrack.Service.impl;

import com.demo.fintrack.Domain.CategoryType;
import com.demo.fintrack.Domain.DTO.TransactionPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionResponseDTO;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Domain.Entity.Transaction;
import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Domain.TransactionType;
import com.demo.fintrack.Mapper.TransactionMapperImpl;
import com.demo.fintrack.Repository.CategoryRepository;
import com.demo.fintrack.Repository.TransactionRepository;
import com.demo.fintrack.Repository.UserRepository;
import com.demo.fintrack.Service.Exception.CategoryNotFoundException;
import com.demo.fintrack.Service.Exception.InvalidCategoryTypeException;
import com.demo.fintrack.Service.Exception.TransactionNotFoundException;
import com.demo.fintrack.Service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapperImpl transactionMapper;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapperImpl transactionMapper, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

//    create
    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {

        Category category = categoryRepository.findById(transactionRequestDTO.getCategoryId()).orElseThrow(()-> new CategoryNotFoundException("Category not found"));
        Transaction transaction = transactionMapper.toEntity(transactionRequestDTO);

        if(CategoryType.valueOf(transactionRequestDTO.getType().name()) != category.getType()){
            throw new InvalidCategoryTypeException("Check the type of the catgeory and transaction");
        }

        transaction.setCategory(category);
        User user = userRepository.findById(UUID.fromString("d6d3453c-a1d3-46c5-a811-54eb5896326d")).orElseThrow(() -> new RuntimeException("User not found"));
        transaction.setUser(user);
        return transactionMapper.toTransactionResponseDTO(transactionRepository.save(transaction));
    }

//    get
    @Override
    public List<TransactionResponseDTO> getAllTransaction() {

        List<Transaction> transactions = transactionRepository.findByUser_Id( UUID.fromString("d6d3453c-a1d3-46c5-a811-54eb5896326d") );

        return transactionMapper.toTransactionResponseDtoList(transactions);
    }

    @Override
    public List<TransactionResponseDTO> getAllTransaction(TransactionType type) {
        List<Transaction> transactions = transactionRepository.findByUser_IdAndType( UUID.fromString("d6d3453c-a1d3-46c5-a811-54eb5896326d") , type );
        return transactionMapper.toTransactionResponseDtoList(transactions);
    }

    @Override
    public TransactionResponseDTO getTransactionByID(UUID id) {
        return transactionMapper.toTransactionResponseDTO(transactionRepository.findByIdAndUser_Id(id, UUID.fromString("d6d3453c-a1d3-46c5-a811-54eb5896326d")).orElseThrow(() -> new TransactionNotFoundException("Transaction not found")));
    }

//    update
    @Override
    public TransactionResponseDTO updateTransaction(TransactionPatchRequestDTO patchRequestDTO, UUID transactionID) {
       Transaction transaction = transactionRepository.findByIdAndUser_Id(transactionID , UUID.fromString("d6d3453c-a1d3-46c5-a811-54eb5896326d") ).map(existingUser -> {
           Optional.ofNullable(patchRequestDTO.getAmount()).ifPresent(existingUser::setAmount);
           Optional.ofNullable(patchRequestDTO.getDate()).ifPresent(existingUser::setDate);
           Optional.ofNullable(patchRequestDTO.getDescription()).ifPresent(existingUser::setDescription);

           if(patchRequestDTO.getCategoryId() != null && patchRequestDTO.getType() != null){
               Category category = categoryRepository.findById(patchRequestDTO.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found"));

               if(CategoryType.valueOf(patchRequestDTO.getType().name()) != category.getType()){
                   throw new InvalidCategoryTypeException("Check the type of Transaction and Category");
               }

               existingUser.setCategory(category);
               existingUser.setType(patchRequestDTO.getType());

           } else if(patchRequestDTO.getCategoryId() != null ) {
               Category category = categoryRepository.findById(patchRequestDTO.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found"));

               if(CategoryType.valueOf(existingUser.getType().name()) != category.getType()){
                   throw new InvalidCategoryTypeException("Check the type of Transaction and Category");
               }
               existingUser.setCategory(category);
           } else if(patchRequestDTO.getType() != null){

               if(CategoryType.valueOf(patchRequestDTO.getType().name()) != existingUser.getCategory().getType()){
                   throw new InvalidCategoryTypeException("Check the type of Transaction and Category");
               }

               existingUser.setType(patchRequestDTO.getType());
           }

           return transactionRepository.save(existingUser);
       }).orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
       return transactionMapper.toTransactionResponseDTO(transaction);
    }

//    delete
    @Override
    public void deleteTransaction(UUID id) {
        Transaction transaction = transactionRepository.findByIdAndUser_Id(id,UUID.fromString("d6d3453c-a1d3-46c5-a811-54eb5896326d")).orElseThrow(() -> new TransactionNotFoundException("Transaction Not found"));
        transactionRepository.delete(transaction);
    }




}
