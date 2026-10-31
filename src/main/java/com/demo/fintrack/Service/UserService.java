package com.demo.fintrack.Service;

import com.demo.fintrack.Domain.DTO.UserDTOs.UserPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserResponseDTO;




public interface UserService {

    UserResponseDTO createUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById();

    UserResponseDTO updateUser(UserPatchRequestDTO requestDTO);

    void deleteUser();

}
