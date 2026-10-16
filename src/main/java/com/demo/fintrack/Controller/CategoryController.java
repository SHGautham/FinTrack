package com.demo.fintrack.Controller;

import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Service.impl.CategoryServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/createCategory")
    public void createCategory(@RequestBody Category category){
        categoryService.createCategory(category);
    }

    @GetMapping("/getall")
    public List<Category> getAll(){
        return categoryService.getallCategory();
    }
}
