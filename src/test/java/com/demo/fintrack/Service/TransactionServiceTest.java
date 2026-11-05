package com.demo.fintrack.Service;

import com.demo.fintrack.Domain.CategoryType;
import com.demo.fintrack.Domain.DTO.TransactionDTOs.TransactionPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionDTOs.TransactionRequestDTO;
import com.demo.fintrack.Domain.DTO.TransactionDTOs.TransactionResponseDTO;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Domain.Entity.Transaction;
import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Domain.TransactionType;
import com.demo.fintrack.Mapper.TransactionMapper;
import com.demo.fintrack.Repository.CategoryRepository;
import com.demo.fintrack.Repository.TransactionRepository;
import com.demo.fintrack.Repository.UserRepository;
import com.demo.fintrack.Service.Exception.CategoryNotFoundException;
import com.demo.fintrack.Service.Exception.InvalidCategoryTypeException;
import com.demo.fintrack.Service.Exception.TransactionNotFoundException;
import com.demo.fintrack.Service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final UUID USER_ID =
            UUID.fromString("464e185b-05c4-4d68-9b4c-2945b13c2703");

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // Test data helpers

    private User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setName("Test User");
        user.setEmail("test@example.com");
        return user;
    }

    private Category createExpenseCategory() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);
        category.setUser(createUser());
        return category;
    }

    private Transaction createTransaction(Category category) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(new BigDecimal("2000.00"));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setDate(LocalDate.of(2026, 10, 16));
        transaction.setDescription("Dinner");
        transaction.setCategory(category);
        transaction.setUser(createUser());
        return transaction;
    }

    private TransactionRequestDTO createRequest(Category category) {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(new BigDecimal("2000.00"));
        request.setType(TransactionType.EXPENSE);
        request.setDate(LocalDate.of(2026, 10, 16));
        request.setDescription("Dinner");
        request.setCategoryId(category.getId());
        return request;
    }

    private TransactionResponseDTO createResponse(Transaction transaction) {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType());
        response.setDate(transaction.getDate());
        response.setDescription(transaction.getDescription());
        response.setCategoryName(transaction.getCategory().getName());
        return response;
    }

    // CREATE

    @Test
    void createTransactionTest() {
        Category category = createExpenseCategory();
        TransactionRequestDTO request = createRequest(category);
        Transaction transaction = createTransaction(category);
        Transaction saved = createTransaction(category);
        TransactionResponseDTO response = createResponse(saved);

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(createUser()));
        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));
        when(transactionMapper.toEntity(request))
                .thenReturn(transaction);
        when(transactionRepository.save( org.mockito.ArgumentMatchers.any(Transaction.class)))
                .thenReturn(saved);
        when(transactionMapper.toTransactionResponseDTO(saved))
                .thenReturn(response);

        TransactionResponseDTO actual =
                transactionService.createTransaction(request);

        assertNotNull(actual);
        assertEquals(saved.getId(), actual.getId());
        assertEquals(new BigDecimal("2000.00"), actual.getAmount());
        assertEquals("Food", actual.getCategoryName());

        verify(transactionRepository).save( org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void createTransactionCategoryNotFoundTest() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(new BigDecimal("500"));
        request.setType(TransactionType.EXPENSE);
        request.setDate(LocalDate.now());
        request.setCategoryId(UUID.randomUUID());

        when(categoryRepository.findById(request.getCategoryId()))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> transactionService.createTransaction(request));

        verify(transactionRepository, never()).save( org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void createTransactionInvalidCategoryTypeTest() {
        Category category = createExpenseCategory();
        TransactionRequestDTO request = createRequest(category);
        request.setType(TransactionType.INCOME);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        assertThrows(InvalidCategoryTypeException.class,
                () -> transactionService.createTransaction(request));

        verify(transactionRepository, never()).save( org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    // READ: ALL

    @Test
    void getAllTransactionTest() {
        List<Transaction> transactions =
                List.of(createTransaction(createExpenseCategory()));
        List<TransactionResponseDTO> responses = List.of(
                createResponse(transactions.get(0)));

        when(transactionRepository.findByUser_Id(USER_ID))
                .thenReturn(transactions);
        when(transactionMapper.toTransactionResponseDtoList(transactions))
                .thenReturn(responses);

        List<TransactionResponseDTO> actual =
                transactionService.getAllTransaction();

        assertEquals(1, actual.size());
        verify(transactionRepository).findByUser_Id(USER_ID);
    }

    @Test
    void getAllTransactionEmptyTest() {
        List<Transaction> emptyTransactions = List.of();
        List<TransactionResponseDTO> emptyResponses = List.of();

        when(transactionRepository.findByUser_Id(USER_ID))
                .thenReturn(emptyTransactions);
        when(transactionMapper.toTransactionResponseDtoList(emptyTransactions))
                .thenReturn(emptyResponses);

        List<TransactionResponseDTO> actual =
                transactionService.getAllTransaction();

        assertTrue(actual.isEmpty());
    }

    // READ: FILTER BY TYPE

    @Test
    void getTransactionByTypeTest() {
        List<Transaction> transactions =
                List.of(createTransaction(createExpenseCategory()));
        List<TransactionResponseDTO> responses =
                List.of(createResponse(transactions.get(0)));

        when(transactionRepository.findByUser_IdAndType(
                USER_ID, TransactionType.EXPENSE))
                .thenReturn(transactions);
        when(transactionMapper.toTransactionResponseDtoList(transactions))
                .thenReturn(responses);

        List<TransactionResponseDTO> actual =
                transactionService.getAllTransaction(TransactionType.EXPENSE);

        assertEquals(1, actual.size());
        assertEquals(TransactionType.EXPENSE, actual.get(0).getType());
    }

    @Test
    void getTransactionByTypeEmptyTest() {
        List<Transaction> emptyTransactions = List.of();

        when(transactionRepository.findByUser_IdAndType(
                USER_ID, TransactionType.EXPENSE))
                .thenReturn(emptyTransactions);
        when(transactionMapper.toTransactionResponseDtoList(emptyTransactions))
                .thenReturn(List.of());

        List<TransactionResponseDTO> actual =
                transactionService.getAllTransaction(TransactionType.EXPENSE);

        assertTrue(actual.isEmpty());
    }

    // READ: BY ID

    @Test
    void getTransactionByIDTest() {
        Category category = createExpenseCategory();
        Transaction transaction = createTransaction(category);
        TransactionResponseDTO response = createResponse(transaction);

        when(transactionRepository.findByIdAndUser_Id(
                transaction.getId(), USER_ID))
                .thenReturn(Optional.of(transaction));
        when(transactionMapper.toTransactionResponseDTO(transaction))
                .thenReturn(response);

        TransactionResponseDTO actual =
                transactionService.getTransactionByID(transaction.getId());

        assertEquals(transaction.getId(), actual.getId());
    }

    @Test
    void getTransactionByIDNotFoundTest() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.findByIdAndUser_Id(transactionId, USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.getTransactionByID(transactionId));
    }

    // UPDATE: PATCH

    @Test
    void updateTransactionAmountTest() {
        Category category = createExpenseCategory();
        Transaction existing = createTransaction(category);
        UUID transactionId = existing.getId();

        TransactionPatchRequestDTO patch = new TransactionPatchRequestDTO();
        patch.setAmount(new BigDecimal("1800.00"));

        when(transactionRepository.findByIdAndUser_Id(transactionId, USER_ID))
                .thenReturn(Optional.of(existing));
        when(transactionRepository.save( org.mockito.ArgumentMatchers.any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionMapper.toTransactionResponseDTO(existing))
                .thenReturn(createResponse(existing));

        TransactionResponseDTO actual =
                transactionService.updateTransaction(patch, transactionId);

        assertEquals(new BigDecimal("1800.00"), existing.getAmount());
        assertEquals(TransactionType.EXPENSE, existing.getType());
        assertEquals(category, existing.getCategory());
        assertNotNull(actual);
        verify(transactionRepository).save(existing);
    }

    @Test
    void updateTransactionNotFoundTest() {
        UUID transactionId = UUID.randomUUID();
        TransactionPatchRequestDTO patch = new TransactionPatchRequestDTO();

        when(transactionRepository.findByIdAndUser_Id(transactionId, USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.updateTransaction(patch, transactionId));

        verify(transactionRepository, never()).save( org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void updateTransactionCategoryNotFoundTest() {
        Category category = createExpenseCategory();
        Transaction existing = createTransaction(category);
        UUID transactionId = existing.getId();
        UUID missingCategoryId = UUID.randomUUID();

        TransactionPatchRequestDTO patch = new TransactionPatchRequestDTO();
        patch.setCategoryId(missingCategoryId);

        when(transactionRepository.findByIdAndUser_Id(transactionId, USER_ID))
                .thenReturn(Optional.of(existing));
        when(categoryRepository.findById(missingCategoryId))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> transactionService.updateTransaction(patch, transactionId));

        verify(transactionRepository, never()).save( org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void updateTransactionInvalidCategoryTypeTest() {
        Category category = createExpenseCategory();
        Transaction existing = createTransaction(category);
        UUID transactionId = existing.getId();

        TransactionPatchRequestDTO patch = new TransactionPatchRequestDTO();
        patch.setType(TransactionType.INCOME);

        when(transactionRepository.findByIdAndUser_Id(transactionId, USER_ID))
                .thenReturn(Optional.of(existing));

        assertThrows(InvalidCategoryTypeException.class,
                () -> transactionService.updateTransaction(patch, transactionId));

        verify(transactionRepository, never()).save( org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    // DELETE

    @Test
    void deleteTransactionTest() {
        Category category = createExpenseCategory();
        Transaction transaction = createTransaction(category);
        UUID transactionId = transaction.getId();

        when(transactionRepository.findByIdAndUser_Id(transactionId, USER_ID))
                .thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository).findByIdAndUser_Id(transactionId, USER_ID);
        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransactionNotFoundTest() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.findByIdAndUser_Id(transactionId, USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.deleteTransaction(transactionId));

        verify(transactionRepository, never()).deleteById(transactionId);
    }
}