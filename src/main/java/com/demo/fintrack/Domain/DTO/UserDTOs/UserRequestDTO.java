package com.demo.fintrack.Domain.DTO.UserDTOs;

import com.demo.fintrack.Domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private Gender gender;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank
    @Email
    private String email;
}
