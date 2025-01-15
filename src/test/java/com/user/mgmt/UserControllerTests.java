package com.user.mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.mgmt.users.ErrorResponse;
import com.user.mgmt.users.User;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    static ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_KEY = "apikey";
    private static final String API_KEY_VAL = "some-api-key";

    @Test
    public void testGetUsers() throws Exception {
        String encodedToken = getEncodedBearerToken(API_KEY_VAL, "viewer");

        ResultActions response = mockMvc.perform(get("/users")
                .header("Authorization", String.format("Bearer %s", encodedToken)));
        String responseStr = response.andReturn().getResponse().getContentAsString();
        System.out.println("response: " + responseStr);
        final User[] users = objectMapper.readValue(responseStr, new TypeReference<User[]>() {
        });
        Assertions.assertEquals(2, users.length);
    }

    @Test
    public void testGetUsersMissingAuthorization() throws Exception {
        String encodedToken = getEncodedBearerToken(API_KEY_VAL,null);

        ResultActions response = mockMvc.perform(get("/users")
                .header("Authorization", String.format("Bearer %s", encodedToken)));
        MockHttpServletResponse mockResponse = response.andReturn().getResponse();
        final ErrorResponse errorResponse = objectMapper.readValue(mockResponse.getErrorMessage(), new TypeReference<ErrorResponse>() {
        });
        Assertions.assertEquals(403, mockResponse.getStatus(), "unexpected response code");
        Assertions.assertEquals(ErrorResponse.ErrorMessages.FORBIDDEN.toString(), errorResponse.getError());
    }

    @Test
    public void testGetUsersMissingApiKey() throws Exception {
        String encodedToken = getEncodedBearerToken(null, null);
        ResultActions response = mockMvc.perform(get("/users")
                .header("Authorization", String.format("Bearer %s", encodedToken)));
        MockHttpServletResponse mockResponse = response.andReturn().getResponse();
        System.out.println("response: " + response.andReturn().getResponse().getErrorMessage());
        final ErrorResponse errorResponse = objectMapper.readValue(mockResponse.getErrorMessage(), new TypeReference<ErrorResponse>() {
        });
        Assertions.assertEquals(401, mockResponse.getStatus(), "unexpected response code");
        Assertions.assertEquals(ErrorResponse.ErrorMessages.UNAUTHENTICATED.toString(), errorResponse.getError());
    }

    @Test
    public void testGetUsersAuthFailure() throws Exception {
        String encodedToken = getEncodedBearerToken("WRONG-KEY", "viewer");
        ResultActions response = mockMvc.perform(get("/users")
                .header("Authorization", String.format("Bearer %s", encodedToken)));
        MockHttpServletResponse mockResponse = response.andReturn().getResponse();
        System.out.println("response: " + response.andReturn().getResponse().getErrorMessage());
        final ErrorResponse errorResponse = objectMapper.readValue(mockResponse.getErrorMessage(), new TypeReference<ErrorResponse>() {
        });
        Assertions.assertEquals(401, mockResponse.getStatus(), "unexpected response code");
        Assertions.assertEquals(ErrorResponse.ErrorMessages.UNAUTHENTICATED.toString(), errorResponse.getError());
    }

    @Test
    public void testGetUsersAuthorizationFailure() throws Exception {
        String encodedToken = getEncodedBearerToken(API_KEY_VAL, "admin");
        ResultActions response = mockMvc.perform(get("/users")
                .header("Authorization", String.format("Bearer %s", encodedToken)));
        MockHttpServletResponse mockResponse = response.andReturn().getResponse();
        System.out.println("response: " + response.andReturn().getResponse().getErrorMessage());
        final ErrorResponse errorResponse = objectMapper.readValue(mockResponse.getErrorMessage(), new TypeReference<ErrorResponse>() {
        });
        Assertions.assertEquals(403, mockResponse.getStatus(), "unexpected response code");
        Assertions.assertEquals(ErrorResponse.ErrorMessages.FORBIDDEN.toString(), errorResponse.getError());
    }

    String getEncodedBearerToken(@Nullable String apiKeyValue, @Nullable String roleValue) {
        final Map<String, String> payload = new HashMap<>();

        if (roleValue != null) payload.put(API_KEY, apiKeyValue);
        if (roleValue != null) payload.put("role", roleValue);

        return getEncodedBearerToken(payload);
    }

    String getEncodedBearerToken(Map<String, String> tokenPayload) {
        try {
            String tokenString = objectMapper.writeValueAsString(tokenPayload);
            return Base64.getEncoder().encodeToString(tokenString.getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
