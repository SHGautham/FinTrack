package com.demo.fintrack.Domain.DTO.CategoryDTO;

import com.demo.fintrack.Domain.CategoryType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryPatchRequestDTO {


    private String name;

    private CategoryType type;

}
