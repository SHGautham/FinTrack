package com.demo.fintrack.Controller;

import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryRequestDTO;
import com.demo.fintrack.Domain.DTO.CategoryDTO.CategoryResponseDTO;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Service.CategoryService;
import com.demo.fintrack.Service.impl.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/createCategory")
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO){
        return new ResponseEntity<>(categoryService.createCategory(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategory(){
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @PatchMapping("/updateCategory/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@Valid @RequestBody CategoryPatchRequestDTO patchRequestDTO, @PathVariable UUID categoryId){
        return new ResponseEntity<>(categoryService.updateCategory(patchRequestDTO, categoryId), HttpStatus.OK);
    }

    @DeleteMapping("/deleteCategory/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId){
        categoryService.deleteCategory(categoryId);
        return  ResponseEntity.noContent().build();
    }

}
