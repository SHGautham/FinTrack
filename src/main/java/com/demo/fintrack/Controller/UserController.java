package com.demo.fintrack.Controller;

import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public void createUser(@RequestBody User user){
        userService.createUser(user);
    }

    @GetMapping("/getAllUser")
    public List<User> getAllUser(){
        return userService.getallUser();
    }

}
