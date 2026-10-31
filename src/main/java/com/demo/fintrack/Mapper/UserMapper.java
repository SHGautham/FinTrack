package com.demo.fintrack.Mapper;

import com.demo.fintrack.Domain.DTO.UserDTOs.UserRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserResponseDTO;
import com.demo.fintrack.Domain.Entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO requestDTO){
        User user = new User();
        user.setName(requestDTO.getName());
        user.setGender(requestDTO.getGender());
        user.setDateOfBirth(requestDTO.getDateOfBirth());
        user.setEmail(requestDTO.getEmail());
        return user;
    }

    public UserResponseDTO toUserResponseDTO(User user){
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setName(user.getName());
        responseDTO.setGender(user.getGender());
        responseDTO.setAge(Period.between(user.getDateOfBirth() , LocalDate.now()).getYears());
        responseDTO.setEmail(user.getEmail());
        return responseDTO;
    }
}
