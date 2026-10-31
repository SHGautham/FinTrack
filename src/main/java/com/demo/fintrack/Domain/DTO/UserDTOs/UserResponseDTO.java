package com.demo.fintrack.Domain.DTO.UserDTOs;

import com.demo.fintrack.Domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDTO {

    private String name;

    private Gender gender;

    private int age;

    private String email;
}
