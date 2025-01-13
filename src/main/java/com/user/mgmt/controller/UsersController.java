package com.user.mgmt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    @GetMapping("/")
    public String root() {
        return "Greetings from User Management service!";
    }
}
