package com.demo.fintrack.Controller;

import com.demo.fintrack.Domain.DTO.UserDTOs.UserPatchRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserRequestDTO;
import com.demo.fintrack.Domain.DTO.UserDTOs.UserResponseDTO;
import com.demo.fintrack.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO){
        return new ResponseEntity<>(userService.createUser(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponseDTO> getById(){
        return new ResponseEntity<>(userService.getUserById(), HttpStatus.OK);
    }

    @PatchMapping("/updateUser")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserPatchRequestDTO requestDTO){
        return new ResponseEntity<>(userService.updateUser(requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Void> deleteUser(){
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }

}
