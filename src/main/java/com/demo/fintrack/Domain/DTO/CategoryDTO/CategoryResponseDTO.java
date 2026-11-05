package com.demo.fintrack.Domain.DTO.CategoryDTO;

import com.demo.fintrack.Domain.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryResponseDTO {

    private UUID id;

    private String name;

    private CategoryType type;

    private String imageKey;

    private boolean isUniversal;
}