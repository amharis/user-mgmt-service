package com.user.mgmt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.mgmt.model.ErrorResponse;
import com.user.mgmt.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.Base64;
import java.sql.Array;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UsersController {
    Logger logger = LoggerFactory.getLogger(UsersController.class);
    static ObjectMapper objectMapper = new ObjectMapper();

    //this value should come from env variable, adding a default for simplicity
    @Value("${api-key-value:some-api-key}")
    private String apiKeyValue;

    @GetMapping("/")
    public String root() {
        return "Greetings from User Management service!";
    }

    @GetMapping("/users")
    public User[] getUsers(@RequestHeader("Authorization") String bearerToken,
                           @RequestParam String role) throws JsonProcessingException {
        logger.info("Here is bearer token: {}", bearerToken);
        logger.info("Here is role: {}", role);

        Map<String, Object> deserializedTokenPayload;
        try {
            //String stringPattern = "Bearer\\s([\\d|a-z|A-Z]{64})";
            String stringPattern = "Bearer\\s([A-Za-z0-9+/]*={0,2})";
            Pattern pattern = Pattern.compile(stringPattern);
            Matcher m = pattern.matcher(bearerToken);

            if (!m.find())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, objectMapper.writeValueAsString(
                        new ErrorResponse("Bearer token is invalid")));
            String token = m.group(1);
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decodedTokenString = new String(decodedBytes);
            deserializedTokenPayload = objectMapper.readValue(decodedTokenString, Map.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, objectMapper.writeValueAsString(
                    new ErrorResponse("Failed to parse bearer token")));
        }

        // TODO add validation layer

        if (!authenticateRequest((String) deserializedTokenPayload.get(API_KEY))) {
            ErrorResponse errorMessage = new ErrorResponse(ErrorResponse.ErrorMessages.UNAUTHENTICATED.toString());
            try {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, objectMapper.writeValueAsString(errorMessage));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        if (!authorizeRequest((String) deserializedTokenPayload.get(ROLE))) {
            ErrorResponse errorMessage = new ErrorResponse(ErrorResponse.ErrorMessages.FORBIDDEN.toString());
            try {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, objectMapper.writeValueAsString(errorMessage));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return users;
    }
    private boolean authenticateRequest(String receivedApiKeyValue) {
        return apiKeyValue.equals(receivedApiKeyValue);
    }

    private boolean authorizeRequest(String role) {
        return VIEWER_ROLE.equals(role);
    }

}
