package com.demo.fintrack.Service.impl;

import com.demo.fintrack.Domain.Entity.User;
import com.demo.fintrack.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl {

    private final UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user){
        userRepository.save(user);
    }

    public List<User> getallUser(){
        return userRepository.findAll();
    }
}
