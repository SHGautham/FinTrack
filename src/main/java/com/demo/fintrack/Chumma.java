package com.demo.fintrack;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Chumma {

    @GetMapping("/")
    public String greeting(){
        return "Welcome to new begining";
    }
}
