package com.user.mgmt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.mgmt.users.ErrorResponse;
import com.user.mgmt.users.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    static ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_KEY = "some-api-key";

    @Test
    public void testGetUsers() throws Exception {
        String encodedToken = getEncodedBearerToken(API_KEY, "viewer");

        ResultActions response = mockMvc.perform(get("/users")
                .header("Authorization", String.format("Bearer %s", encodedToken)));
        String responseStr = response.andReturn().getResponse().getContentAsString();
        System.out.println("response: " + responseStr);
        final User[] users = objectMapper.readValue(responseStr, new TypeReference<User[]>() {
        });
        Assertions.assertEquals(2, users.length);
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
        String encodedToken = getEncodedBearerToken(API_KEY, "admin");
        ResultActions response = mockMvc.perform(get("/users")
                .header("Authorization", String.format("Bearer %s", encodedToken)));
        MockHttpServletResponse mockResponse = response.andReturn().getResponse();
        System.out.println("response: " + response.andReturn().getResponse().getErrorMessage());
        final ErrorResponse errorResponse = objectMapper.readValue(mockResponse.getErrorMessage(), new TypeReference<ErrorResponse>() {
        });
        Assertions.assertEquals(403, mockResponse.getStatus(), "unexpected response code");
        Assertions.assertEquals(ErrorResponse.ErrorMessages.FORBIDDEN.toString(), errorResponse.getError());
    }

    String getEncodedBearerToken(String apiKey, String roleValue) {
        String raw =  "{ \"api-key\" : \""+ apiKey +"\", " +
                "\"role\" : \""+ roleValue +"\"}";
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }
}
