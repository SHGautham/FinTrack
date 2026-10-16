package com.demo.fintrack.Service.impl;

import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl {

    private final CategoryRepository categoryRepository;


    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void createCategory(Category category){
        categoryRepository.save(category);
    }

    public List<Category> getallCategory(){
        return categoryRepository.findAll();
    }
}
