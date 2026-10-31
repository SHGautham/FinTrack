package com.demo.fintrack.Service;


import com.demo.fintrack.Domain.DTO.UserDTOs.UserPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserResponseDTO;
import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Domain.Gender;
import com.demo.fintrack.Mapper.UserMapper;
import com.demo.fintrack.Repository.UserRepository;
import com.demo.fintrack.Service.Exception.UserAlreadyFoundException;
import com.demo.fintrack.Service.Exception.UserNotFoundException;
import com.demo.fintrack.Service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final UUID userId = UUID.fromString("464e185b-05c4-4d68-9b4c-2945b13c2703");

    private User user;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName("Gautham");
        user.setEmail("gautham@gmail.com");
        user.setGender(Gender.MALE);
        user.setDateOfBirth(LocalDate.of(2004, 5, 15));

        responseDTO = new UserResponseDTO();
        responseDTO.setName("Gautham");
        responseDTO.setEmail("gautham@gmail.com");
        responseDTO.setGender(Gender.MALE);
        responseDTO.setAge(22);
    }


    @Test
    void createUser_success() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setName("Gautham");
        requestDTO.setEmail("gautham@gmail.com");
        requestDTO.setGender(Gender.MALE);
        requestDTO.setDateOfBirth(LocalDate.of(2004, 5, 15));

        when(userRepository.existsByEmail(requestDTO.getEmail()))
                .thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(requestDTO);

        assertNotNull(result);
        assertEquals("Gautham", result.getName());
        assertEquals("gautham@gmail.com", result.getEmail());

        verify(userRepository).existsByEmail(requestDTO.getEmail());
        verify(userRepository).save(user);
        verify(userMapper).toEntity(requestDTO);
        verify(userMapper).toUserResponseDTO(user);
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setEmail("gautham@gmail.com");

        when(userRepository.existsByEmail(requestDTO.getEmail()))
                .thenReturn(true);

        assertThrows(UserAlreadyFoundException.class,
                () -> userService.createUser(requestDTO));

        verify(userRepository).existsByEmail(requestDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toEntity(any(UserRequestDTO.class));
    }


    @Test
    void getUserById_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserById();

        assertNotNull(result);
        assertEquals("Gautham", result.getName());
        assertEquals("gautham@gmail.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userMapper).toUserResponseDTO(user);
    }

    @Test
    void getUserById_userNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById());

        verify(userRepository).findById(userId);
        verify(userMapper, never()).toUserResponseDTO(any(User.class));
    }


    @Test
    void updateUser_success() {
        UserPatchRequestDTO requestDTO = new UserPatchRequestDTO();
        requestDTO.setName("Gautham Kumar");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO updatedResponse = new UserResponseDTO();
        updatedResponse.setName("Gautham Kumar");
        updatedResponse.setEmail("gautham@gmail.com");
        updatedResponse.setGender(Gender.MALE);

        when(userMapper.toUserResponseDTO(user)).thenReturn(updatedResponse);

        UserResponseDTO result = userService.updateUser(requestDTO);

        assertNotNull(result);
        assertEquals("Gautham Kumar", result.getName());
        assertEquals("gautham@gmail.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userMapper).toUserResponseDTO(user);
    }


    @Test
    void updateUser_newEmail_success() {
        UserPatchRequestDTO requestDTO = new UserPatchRequestDTO();
        requestDTO.setEmail("newemail@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("newemail@gmail.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUser(requestDTO);

        assertNotNull(result);
        assertEquals("newemail@gmail.com", user.getEmail());

        verify(userRepository).existsByEmail("newemail@gmail.com");
        verify(userRepository).save(user);
    }


    @Test
    void updateUser_duplicateEmail_throwsException() {
        UserPatchRequestDTO requestDTO = new UserPatchRequestDTO();
        requestDTO.setEmail("existing@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@gmail.com")).thenReturn(true);

        assertThrows(UserAlreadyFoundException.class,
                () -> userService.updateUser(requestDTO));

        verify(userRepository).existsByEmail("existing@gmail.com");
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUser_userNotFound_throwsException() {
        UserPatchRequestDTO requestDTO = new UserPatchRequestDTO();
        requestDTO.setName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(requestDTO));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void deleteUser_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser();

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }


    @Test
    void deleteUser_userNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }


}

