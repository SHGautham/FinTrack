package com.demo.fintrack.Service.impl;

import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryResponseDTO;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Mapper.CategoryMapper;
import com.demo.fintrack.Repository.CategoryRepository;
import com.demo.fintrack.Repository.UserRepository;
import com.demo.fintrack.Service.CategoryService;
import com.demo.fintrack.Service.Exception.CategoryAlreadyExistException;
import com.demo.fintrack.Service.Exception.CategoryNotFoundException;
import com.demo.fintrack.Service.Exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final UUID userId = UUID.fromString("464e185b-05c4-4d68-9b4c-2945b13c2703");

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.userRepository = userRepository;
    }


    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        if(categoryRepository.existsByNameAndTypeUniversalOrUser(requestDTO.getName(), requestDTO.getType(), userId)){
            throw new CategoryAlreadyExistException("This category already exist");
        }
        Category category = categoryMapper.toEntity(requestDTO);
        category.setImageKey("default");
        category.setUser(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found")));

        return categoryMapper.toResponseDTO(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponseDTO> getAll() {
        return categoryMapper.responseDTOList(categoryRepository.findAllByUserIsNull_OrUser_Id(userId));
    }

    @Override
    public CategoryResponseDTO updateCategory(CategoryPatchRequestDTO patchRequestDTO, UUID categoryId) {

        Category existingCategory = categoryRepository.findByIdAndUser_Id(categoryId, userId).orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        if(patchRequestDTO.getName() != null){
            if (patchRequestDTO.getName().isBlank()){
                throw new RuntimeException("Name of the category should not be empty");
            }
            existingCategory.setName(patchRequestDTO.getName());
        }
        Optional.ofNullable(patchRequestDTO.getType()).ifPresent(existingCategory::setType);

        if(categoryRepository.existsByNameAndTypeAndUser_IdAndIdNot(existingCategory.getName(), existingCategory.getType(), userId, existingCategory.getId())){
            throw new CategoryAlreadyExistException("This category already exist");
        }

        return categoryMapper.toResponseDTO(categoryRepository.save(existingCategory));
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findByIdAndUser_Id(categoryId, userId).orElseThrow(() -> new CategoryNotFoundException("Category Not found"));
        categoryRepository.delete(category);
    }
}
