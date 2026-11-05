package com.demo.fintrack.Repository;

import com.demo.fintrack.Domain.CategoryType;
import com.demo.fintrack.Domain.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByNameAndTypeAndUserIsNull(String name, CategoryType type);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.type = :type AND (c.user IS NULL OR c.user.id = :userId)")
    boolean existsByNameAndTypeUniversalOrUser(
            @Param("name") String name,
            @Param("type") CategoryType type,
            @Param("userId") UUID userId
    );

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.type = :type AND (c.user IS NULL OR c.user.id = :userId) AND c.id <> :categoryId")
    boolean existsByNameAndTypeAndUser_IdAndIdNot(
            @Param("name") String name,
            @Param("type") CategoryType type,
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId
    );

    Optional<Category> findByIdAndUser_Id(UUID categoryId, UUID userId);

    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user.id = :userId")
    List<Category> findAllByUserIsNull_OrUser_Id(@Param("userId") UUID userId);
}
