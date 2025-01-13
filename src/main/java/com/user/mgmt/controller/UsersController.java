package com.user.mgmt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.mgmt.model.ErrorResponse;
import com.user.mgmt.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Array;

@RestController
public class UsersController {
    static final User user1 = new User("user1","john.doe");
    static final User user2 = new User("user2","jane.smith");

    static final User[] users = { user1, user2 };
    public static final String API_KEY = "some-api-key";

    Logger logger = LoggerFactory.getLogger(UsersController.class);
    static ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public String root() {
        return "Greetings from User Management service!";
    }

    @GetMapping("/users")
    public User[] getUsers(@RequestHeader("Authorization") String bearerToken) {
        logger.info("Here is bearer token: {}", bearerToken);

        if (!authenticateRequest(bearerToken)) {
            ErrorResponse errorMessage = new ErrorResponse(ErrorResponse.ErrorMessages.UNAUTHENTICATED.toString());
            try {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, objectMapper.writeValueAsString(errorMessage));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return users;
    }

    private boolean authenticateRequest(String apiKey) {
        return API_KEY.equals(apiKey);
    }

    private boolean authorizeRequest(String apiKey) {
        return API_KEY.equals(apiKey);
    }

}
