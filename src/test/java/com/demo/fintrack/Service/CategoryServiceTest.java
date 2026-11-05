package com.demo.fintrack.Service;

import com.demo.fintrack.Domain.CategoryType;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryResponseDTO;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Mapper.CategoryMapper;
import com.demo.fintrack.Repository.CategoryRepository;
import com.demo.fintrack.Repository.UserRepository;
import com.demo.fintrack.Service.Exception.CategoryAlreadyExistException;
import com.demo.fintrack.Service.Exception.CategoryNotFoundException;
import com.demo.fintrack.Service.Exception.UserNotFoundException;
import com.demo.fintrack.Service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private final UUID userId =
            UUID.fromString("464e185b-05c4-4d68-9b4c-2945b13c2703");

    private UUID categoryId;
    private Category category;
    private User user;
    private CategoryResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category = new Category();
        category.setId(categoryId);
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);

        user = mock(User.class);
        responseDTO = mock(CategoryResponseDTO.class);
    }

    // CREATE CATEGORY

    @Test
    void createCategory_Success() {
        CategoryRequestDTO requestDTO = mock(CategoryRequestDTO.class);

        when(requestDTO.getName()).thenReturn("Food");
        when(requestDTO.getType()).thenReturn(CategoryType.EXPENSE);
        when(categoryRepository.existsByNameAndTypeUniversalOrUser(
                "Food", CategoryType.EXPENSE, userId
        )).thenReturn(false);
        when(categoryMapper.toEntity(requestDTO)).thenReturn(category);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponseDTO(category)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.createCategory(requestDTO);

        assertSame(responseDTO, result);
        assertEquals("default", category.getImageKey());
        assertSame(user, category.getUser());

        verify(categoryRepository).save(category);
    }

    @Test
    void createCategory_DuplicateCategory_ThrowsException() {
        CategoryRequestDTO requestDTO = mock(CategoryRequestDTO.class);

        when(requestDTO.getName()).thenReturn("Food");
        when(requestDTO.getType()).thenReturn(CategoryType.EXPENSE);
        when(categoryRepository.existsByNameAndTypeUniversalOrUser(
                "Food", CategoryType.EXPENSE, userId
        )).thenReturn(true);

        assertThrows(CategoryAlreadyExistException.class,
                () -> categoryService.createCategory(requestDTO));

        verify(categoryRepository, never()).save(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void createCategory_UserNotFound_ThrowsException() {
        CategoryRequestDTO requestDTO = mock(CategoryRequestDTO.class);

        when(requestDTO.getName()).thenReturn("Food");
        when(requestDTO.getType()).thenReturn(CategoryType.EXPENSE);
        when(categoryRepository.existsByNameAndTypeUniversalOrUser(
                "Food", CategoryType.EXPENSE, userId
        )).thenReturn(false);
        when(categoryMapper.toEntity(requestDTO)).thenReturn(category);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> categoryService.createCategory(requestDTO));

        verify(categoryRepository, never()).save(any());
    }

    // GET ALL CATEGORIES

    @Test
    void getAll_Success() {
        List<Category> categories = List.of(category);
        List<CategoryResponseDTO> expected = List.of(responseDTO);

        when(categoryRepository.findAllByUserIsNull_OrUser_Id(userId))
                .thenReturn(categories);
        when(categoryMapper.responseDTOList(categories)).thenReturn(expected);

        List<CategoryResponseDTO> result = categoryService.getAll();

        assertEquals(expected, result);
        verify(categoryRepository).findAllByUserIsNull_OrUser_Id(userId);
        verify(categoryMapper).responseDTOList(categories);
    }

    // UPDATE CATEGORY

    @Test
    void updateCategory_NameSuccessfullyUpdated() {
        CategoryPatchRequestDTO patchDTO =
                mock(CategoryPatchRequestDTO.class);

        when(patchDTO.getName()).thenReturn("Restaurants");
        when(patchDTO.getType()).thenReturn(null);
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId))
                .thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndTypeAndUser_IdAndIdNot(
                "Restaurants", CategoryType.EXPENSE, userId, categoryId
        )).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponseDTO(category)).thenReturn(responseDTO);

        CategoryResponseDTO result =
                categoryService.updateCategory(patchDTO, categoryId);

        assertSame(responseDTO, result);
        assertEquals("Restaurants", category.getName());
        assertEquals(CategoryType.EXPENSE, category.getType());

        verify(categoryRepository).save(category);
    }

    @Test
    void updateCategory_TypeSuccessfullyUpdated() {
        CategoryPatchRequestDTO patchDTO =
                mock(CategoryPatchRequestDTO.class);

        when(patchDTO.getName()).thenReturn(null);
        when(patchDTO.getType()).thenReturn(CategoryType.INCOME);
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId))
                .thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndTypeAndUser_IdAndIdNot(
                "Food", CategoryType.INCOME, userId, categoryId
        )).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponseDTO(category)).thenReturn(responseDTO);

        CategoryResponseDTO result =
                categoryService.updateCategory(patchDTO, categoryId);

        assertSame(responseDTO, result);
        assertEquals(CategoryType.INCOME, category.getType());
    }

    @Test
    void updateCategory_NoFieldsChanged() {
        CategoryPatchRequestDTO patchDTO =
                mock(CategoryPatchRequestDTO.class);

        when(patchDTO.getName()).thenReturn(null);
        when(patchDTO.getType()).thenReturn(null);
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId))
                .thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndTypeAndUser_IdAndIdNot(
                "Food", CategoryType.EXPENSE, userId, categoryId
        )).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponseDTO(category)).thenReturn(responseDTO);

        CategoryResponseDTO result =
                categoryService.updateCategory(patchDTO, categoryId);

        assertSame(responseDTO, result);
        assertEquals("Food", category.getName());
        assertEquals(CategoryType.EXPENSE, category.getType());
    }

    @Test
    void updateCategory_CategoryNotFound_ThrowsException() {
        CategoryPatchRequestDTO patchDTO =
                mock(CategoryPatchRequestDTO.class);

        when(categoryRepository.findByIdAndUser_Id(categoryId, userId))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.updateCategory(patchDTO, categoryId));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_DuplicateCategory_ThrowsException() {
        CategoryPatchRequestDTO patchDTO =
                mock(CategoryPatchRequestDTO.class);

        when(patchDTO.getName()).thenReturn("Food");
        when(patchDTO.getType()).thenReturn(null);
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId))
                .thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndTypeAndUser_IdAndIdNot(
                "Food", CategoryType.EXPENSE, userId, categoryId
        )).thenReturn(true);

        assertThrows(CategoryAlreadyExistException.class,
                () -> categoryService.updateCategory(patchDTO, categoryId));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_BlankName_ThrowsException() {
        CategoryPatchRequestDTO patchDTO =
                mock(CategoryPatchRequestDTO.class);

        when(patchDTO.getName()).thenReturn("   ");
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId))
                .thenReturn(Optional.of(category));

        assertThrows(RuntimeException.class,
                () -> categoryService.updateCategory(patchDTO, categoryId));

        verify(categoryRepository, never()).save(any());
    }

    // DELETE CATEGORY

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId))
                .thenReturn(Optional.of(category));

        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_CategoryNotFound_ThrowsException() {
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository, never()).delete(any());
    }
}
