package com.demo.fintrack.Service.impl;

import com.demo.fintrack.Domain.DTO.UserDTOs.UserPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserResponseDTO;
import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Mapper.UserMapper;
import com.demo.fintrack.Repository.UserRepository;
import com.demo.fintrack.Service.Exception.UserAlreadyFoundException;
import com.demo.fintrack.Service.Exception.UserNotFoundException;
import com.demo.fintrack.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UUID userId = UUID.fromString("464e185b-05c4-4d68-9b4c-2945b13c2703");

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if(userRepository.existsByEmail(requestDTO.getEmail())){
            throw new UserAlreadyFoundException("User already exist");
        }
        return userMapper.toUserResponseDTO(userRepository.save(userMapper.toEntity(requestDTO)));
    }

    @Override
    public UserResponseDTO getUserById() {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(UserPatchRequestDTO requestDTO) {
        User user = userRepository.findById(userId).map(existingUser -> {
            Optional.ofNullable(requestDTO.getName()).ifPresent(existingUser::setName);
            Optional.ofNullable(requestDTO.getGender()).ifPresent(existingUser::setGender);
            Optional.ofNullable(requestDTO.getDateOfBirth()).ifPresent(existingUser::setDateOfBirth);

            if( requestDTO.getEmail() != null){
                if (userRepository.existsByEmail(requestDTO.getEmail())) {
                    throw new UserAlreadyFoundException("User already exist");
                }
                existingUser.setEmail(requestDTO.getEmail());
            }
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public void deleteUser() {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }
}
