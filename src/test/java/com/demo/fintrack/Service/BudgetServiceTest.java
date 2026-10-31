package com.demo.fintrack.Service;

import com.demo.fintrack.Domain.*;
import com.demo.fintrack.Domain.DTO.BudgetDTOs.BudgetPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetDTOs.BudgetRequestDTO;
import com.demo.fintrack.Domain.DTO.BudgetDTOs.BudgetResponseDTO;
import com.demo.fintrack.Domain.Entity.Budget;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Mapper.BudgetMapper;
import com.demo.fintrack.Repository.BudgetRepository;
import com.demo.fintrack.Repository.CategoryRepository;
import com.demo.fintrack.Repository.TransactionRepository;
import com.demo.fintrack.Repository.UserRepository;
import com.demo.fintrack.Service.Exception.*;
import com.demo.fintrack.Service.impl.BudgetServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private final UUID userId =
            UUID.fromString("464e185b-05c4-4d68-9b4c-2945b13c2703");

    private final UUID categoryId = UUID.randomUUID();

    private final UUID budgetId = UUID.randomUUID();

    private User user;
    private Category category;
    private Budget budget;
    private BudgetRequestDTO requestDTO;
    private BudgetResponseDTO responseDTO;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(userId);

        category = new Category();
        category.setId(categoryId);
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);
        category.setUser(null); // Universal category

        budget = new Budget();
        budget.setId(budgetId);
        budget.setAmount(new BigDecimal("5000"));
        budget.setMonth(YearMonth.of(2026, 10));
        budget.setUser(user);
        budget.setCategory(category);

        requestDTO = new BudgetRequestDTO();
        requestDTO.setAmount(new BigDecimal("5000"));
        requestDTO.setMonth(YearMonth.of(2026, 10));
        requestDTO.setCategoryId(categoryId);

        responseDTO = new BudgetResponseDTO();
    }

    // CREATE BUDGET TEST CASES

    @Test
    void createBudgetTest() {

        when(budgetRepository.existsByUser_IdAndCategory_IdAndMonth(
                userId, categoryId, requestDTO.getMonth()))
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(budgetMapper.toEntity(requestDTO))
                .thenReturn(budget);

        when(budgetRepository.save(budget))
                .thenReturn(budget);

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        when(budgetMapper.toBudgetResponseDTO(
                eq(budget), eq(BigDecimal.ZERO),
                any(), anyInt(), anyBoolean(), anyInt()))
                .thenReturn(responseDTO);

        BudgetResponseDTO result = budgetService.createBudget(requestDTO);

        assertNotNull(result);
        assertSame(responseDTO, result);

        verify(budgetRepository).save(budget);
    }

    @Test
    void createBudgetDuplicateExceptionTest() {

        when(budgetRepository.existsByUser_IdAndCategory_IdAndMonth(
                userId, categoryId, requestDTO.getMonth()))
                .thenReturn(true);

        assertThrows(
                BudgetAlreadyExistException.class,
                () -> budgetService.createBudget(requestDTO)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void createBudgetUserNotFoundExceptionTest() {

        when(budgetRepository.existsByUser_IdAndCategory_IdAndMonth(
                userId, categoryId, requestDTO.getMonth()))
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> budgetService.createBudget(requestDTO)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void createBudgetCategoryNotFoundExceptionTest() {

        when(budgetRepository.existsByUser_IdAndCategory_IdAndMonth(
                userId, categoryId, requestDTO.getMonth()))
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> budgetService.createBudget(requestDTO)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void createBudgetInvalidCategoryTypeExceptionTest() {

        category.setType(CategoryType.INCOME);

        when(budgetRepository.existsByUser_IdAndCategory_IdAndMonth(
                userId, categoryId, requestDTO.getMonth()))
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        assertThrows(
                InvalidCategoryTypeException.class,
                () -> budgetService.createBudget(requestDTO)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void createBudgetCategoryNotBelongToUserExceptionTest() {

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        category.setUser(anotherUser);

        when(budgetRepository.existsByUser_IdAndCategory_IdAndMonth(
                userId, categoryId, requestDTO.getMonth()))
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        assertThrows(
                CategoryNotBelongToUserException.class,
                () -> budgetService.createBudget(requestDTO)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void getBudgetByIDTest() {

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(new BigDecimal("2000"));

        when(budgetMapper.toBudgetResponseDTO(
                eq(budget), eq(new BigDecimal("2000")),
                any(), anyInt(), anyBoolean(), anyInt()))
                .thenReturn(responseDTO);

        BudgetResponseDTO result = budgetService.getBudgetByID(budgetId);

        assertNotNull(result);
        assertSame(responseDTO, result);

        verify(budgetRepository).findByIdAndUser_Id(budgetId, userId);
    }

    @Test
    void getBudgetByIDNotFoundExceptionTest() {

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.empty());

        assertThrows(
                BudgetNotFoundException.class,
                () -> budgetService.getBudgetByID(budgetId)
        );

        verify(transactionRepository, never())
                .totalAmountSpentByCategoryAndDate(any(), any(), any(), any());
    }


    @Test
    void getBudgetByUserIdTest() {

        when(budgetRepository.findByUser_Id(userId))
                .thenReturn(List.of(budget));

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(new BigDecimal("2000"));

        when(budgetMapper.toBudgetResponseDTO(
                eq(budget), eq(new BigDecimal("2000")),
                any(), anyInt(), anyBoolean(), anyInt()))
                .thenReturn(responseDTO);

        List<BudgetResponseDTO> result = budgetService.getBudgetByUserId();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(responseDTO, result.get(0));

        verify(budgetRepository).findByUser_Id(userId);
    }

    @Test
    void getBudgetByUserIdEmptyListTest() {

        when(budgetRepository.findByUser_Id(userId))
                .thenReturn(List.of());

        List<BudgetResponseDTO> result = budgetService.getBudgetByUserId();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepository, never())
                .totalAmountSpentByCategoryAndDate(any(), any(), any(), any());
    }

    @Test
    void getBudgetByMonthAndUserIdTest() {

        YearMonth month = YearMonth.of(2026, 10);

        when(budgetRepository.findByMonthAndUser_Id(month, userId))
                .thenReturn(List.of(budget));

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(new BigDecimal("2000"));

        when(budgetMapper.toBudgetResponseDTO(
                eq(budget), eq(new BigDecimal("2000")),
                any(), anyInt(), anyBoolean(), anyInt()))
                .thenReturn(responseDTO);

        List<BudgetResponseDTO> result =
                budgetService.getBudgetByMonthAndUserId(month);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(responseDTO, result.get(0));

        verify(budgetRepository).findByMonthAndUser_Id(month, userId);
    }

    @Test
    void getBudgetByMonthAndUserIdEmptyListTest() {

        YearMonth month = YearMonth.of(2026, 10);

        when(budgetRepository.findByMonthAndUser_Id(month, userId))
                .thenReturn(List.of());

        List<BudgetResponseDTO> result =
                budgetService.getBudgetByMonthAndUserId(month);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepository, never())
                .totalAmountSpentByCategoryAndDate(any(), any(), any(), any());
    }

    @Test
    void getBudgetByCategoryTest() {

        when(budgetRepository.findByUser_IdAndCategory_Id(userId, categoryId))
                .thenReturn(List.of(budget));

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(new BigDecimal("2000"));

        when(budgetMapper.toBudgetResponseDTO(
                eq(budget), eq(new BigDecimal("2000")),
                any(), anyInt(), anyBoolean(), anyInt()))
                .thenReturn(responseDTO);

        List<BudgetResponseDTO> result =
                budgetService.getBudgetByCategory(categoryId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(responseDTO, result.get(0));

        verify(budgetRepository).findByUser_IdAndCategory_Id(userId, categoryId);
    }

    @Test
    void getBudgetByCategoryEmptyListTest() {

        when(budgetRepository.findByUser_IdAndCategory_Id(userId, categoryId))
                .thenReturn(List.of());

        List<BudgetResponseDTO> result =
                budgetService.getBudgetByCategory(categoryId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepository, never())
                .totalAmountSpentByCategoryAndDate(any(), any(), any(), any());
    }


    @Test
    void updateBudgetTest() {

        BudgetPatchRequestDTO patchDTO = new BudgetPatchRequestDTO();
        patchDTO.setAmount(new BigDecimal("7000"));

        budget.setAmount(new BigDecimal("7000"));

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(budgetRepository.existsByUser_IdAndCategory_IdAndMonthAndIdNot(
                userId, categoryId, budget.getMonth(), budgetId))
                .thenReturn(false);

        when(budgetRepository.save(budget)).thenReturn(budget);

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(new BigDecimal("2000"));

        when(budgetMapper.toBudgetResponseDTO(
                eq(budget), eq(new BigDecimal("2000")),
                any(), anyInt(), anyBoolean(), anyInt()))
                .thenReturn(responseDTO);

        BudgetResponseDTO result = budgetService.updateBudget(patchDTO, budgetId);

        assertNotNull(result);
        assertEquals(new BigDecimal("7000"), budget.getAmount());
        assertSame(responseDTO, result);

        verify(budgetRepository).save(budget);
    }

    @Test
    void updateBudgetNotFoundExceptionTest() {

        BudgetPatchRequestDTO patchDTO = new BudgetPatchRequestDTO();

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.empty());

        assertThrows(
                BudgetNotFoundException.class,
                () -> budgetService.updateBudget(patchDTO, budgetId)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void updateBudgetCategoryNotFoundExceptionTest() {

        BudgetPatchRequestDTO patchDTO = new BudgetPatchRequestDTO();
        patchDTO.setCategoryId(categoryId);

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> budgetService.updateBudget(patchDTO, budgetId)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void updateBudgetInvalidCategoryTypeExceptionTest() {

        BudgetPatchRequestDTO patchDTO = new BudgetPatchRequestDTO();
        patchDTO.setCategoryId(categoryId);

        category.setType(CategoryType.INCOME);

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        assertThrows(
                InvalidCategoryTypeException.class,
                () -> budgetService.updateBudget(patchDTO, budgetId)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void updateBudgetCategoryNotBelongToUserExceptionTest() {

        BudgetPatchRequestDTO patchDTO = new BudgetPatchRequestDTO();
        patchDTO.setCategoryId(categoryId);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        category.setUser(anotherUser);

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        assertThrows(
                CategoryNotBelongToUserException.class,
                () -> budgetService.updateBudget(patchDTO, budgetId)
        );

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void updateBudgetDuplicateExceptionTest() {

        BudgetPatchRequestDTO patchDTO = new BudgetPatchRequestDTO();
        patchDTO.setAmount(new BigDecimal("7000"));

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(budgetRepository.existsByUser_IdAndCategory_IdAndMonthAndIdNot(
                userId, categoryId, budget.getMonth(), budgetId))
                .thenReturn(true);

        assertThrows(
                BudgetAlreadyExistException.class,
                () -> budgetService.updateBudget(patchDTO, budgetId)
        );

        verify(budgetRepository, never()).save(any());
    }


    @Test
    void deleteBudgetTest() {

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        budgetService.deleteBudget(budgetId);

        verify(budgetRepository).findByIdAndUser_Id(budgetId, userId);
        verify(budgetRepository).delete(budget);
    }

    @Test
    void deleteBudgetNotFoundExceptionTest() {

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.empty());

        assertThrows(
                BudgetNotFoundException.class,
                () -> budgetService.deleteBudget(budgetId)
        );

        verify(budgetRepository, never()).delete(any());
    }


    @Test
    void budgetCalculationBelowLimitTest() {

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(new BigDecimal("2000"));

        budgetService.getBudgetByID(budgetId);

        verify(budgetMapper).toBudgetResponseDTO(
                budget,
                new BigDecimal("2000"),
                new BigDecimal("3000"),
                40,
                false,
                0
        );
    }

    @Test
    void budgetCalculationExactlyAtLimitTest() {

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(new BigDecimal("5000"));

        budgetService.getBudgetByID(budgetId);

        verify(budgetMapper).toBudgetResponseDTO(
                budget,
                new BigDecimal("5000"),
                BigDecimal.ZERO,
                100,
                false,
                0
        );
    }

    @Test
    void budgetCalculationExceededLimitTest() {

        when(budgetRepository.findByIdAndUser_Id(budgetId, userId))
                .thenReturn(Optional.of(budget));

        when(transactionRepository.totalAmountSpentByCategoryAndDate(
                eq(userId), eq(categoryId), any(), any()))
                .thenReturn(new BigDecimal("6000"));

        budgetService.getBudgetByID(budgetId);

        verify(budgetMapper).toBudgetResponseDTO(
                budget,
                new BigDecimal("6000"),
                BigDecimal.ZERO,
                120,
                true,
                20
        );
    }


}
