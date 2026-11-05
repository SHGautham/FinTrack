package com.demo.fintrack.Mapper;

import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryResponseDTO;
import com.demo.fintrack.Domain.Entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toResponseDTO(Category category){
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(category.getId());
        responseDTO.setName(category.getName());
        responseDTO.setType(category.getType());
        responseDTO.setImageKey(category.getImageKey());
        responseDTO.setUniversal(category.getUser() == null);
        return responseDTO;
    }

    public Category toEntity(CategoryRequestDTO requestDTO){
        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setType(requestDTO.getType());
        return category;
    }

    public List<CategoryResponseDTO> responseDTOList(List<Category> categories){
        return categories.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
