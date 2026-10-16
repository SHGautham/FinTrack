package com.demo.fintrack.Repository;

import com.demo.fintrack.Domain.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
