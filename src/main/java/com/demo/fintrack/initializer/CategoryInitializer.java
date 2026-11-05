package com.demo.fintrack.initializer;

import com.demo.fintrack.Domain.CategoryType;
import com.demo.fintrack.Domain.Entity.Category;
import com.demo.fintrack.Repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<CategorySeed> universalCategories = List.of(
//                Income category
                // Income Categories
                new CategorySeed("Salary", CategoryType.INCOME, "salary"),
                new CategorySeed("Freelance", CategoryType.INCOME, "freelance"),

// Expense Categories

                new CategorySeed("Groceries", CategoryType.EXPENSE, "groceries"),
                new CategorySeed("Housing & Rent", CategoryType.EXPENSE, "housing-and-rent"),
                new CategorySeed("Utilities", CategoryType.EXPENSE, "utilities"),
                new CategorySeed("Transportation", CategoryType.EXPENSE, "transportation"),
                new CategorySeed("Entertainment", CategoryType.EXPENSE, "entertainment"),
                new CategorySeed("Shopping", CategoryType.EXPENSE, "shopping"),
                new CategorySeed("Healthcare & Medical", CategoryType.EXPENSE, "healthcare-and-medical"),
                new CategorySeed("Subscriptions", CategoryType.EXPENSE, "subscriptions"),
                new CategorySeed("Education", CategoryType.EXPENSE, "education"),
                new CategorySeed("Personal Care", CategoryType.EXPENSE, "personal-care"),
                new CategorySeed("Travel & Vacations", CategoryType.EXPENSE, "travel-and-vacations"),
                new CategorySeed("Pets", CategoryType.EXPENSE, "pets"),
                new CategorySeed("Home Maintenance", CategoryType.EXPENSE, "home-maintenance"),
                new CategorySeed("Fitness & Sports", CategoryType.EXPENSE, "fitness-and-sports"),
                new CategorySeed("Miscellaneous", CategoryType.EXPENSE, "miscellaneous")
        );

        for(CategorySeed seed : universalCategories){
            if(!categoryRepository.existsByNameAndTypeAndUserIsNull(seed.name(), seed.type())){
                Category category = new Category();
                category.setName(seed.name());
                category.setType(seed.type());
                category.setImageKey(seed.imageKey());
                category.setUser(null);
                categoryRepository.save(category);
                System.out.println("Seeded Universal category : "+ seed.name());
            }
        }
    }
    private record CategorySeed(String name, CategoryType type, String imageKey) {}
}
