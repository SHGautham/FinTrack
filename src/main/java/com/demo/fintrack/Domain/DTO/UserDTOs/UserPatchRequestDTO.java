package com.demo.fintrack.Domain.DTO.UserDTOs;

import com.demo.fintrack.Domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPatchRequestDTO {

    @NotBlank
    private String name;

    private Gender gender;

    @Past
    private LocalDate dateOfBirth;

    @Email
    private String email;
}
