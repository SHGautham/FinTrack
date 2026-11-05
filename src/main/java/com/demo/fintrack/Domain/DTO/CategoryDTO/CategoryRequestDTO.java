package com.demo.fintrack.Domain.DTO.CategoryDTO;

import com.demo.fintrack.Domain.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private CategoryType type;
}


