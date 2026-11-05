package com.demo.fintrack.Service;

import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);

    List<CategoryResponseDTO> getAll();

    CategoryResponseDTO updateCategory(CategoryPatchRequestDTO patchRequestDTO, UUID categoryId);

    void deleteCategory(UUID categoryId);

}
