package com.user.mgmt.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UsersController {
    public static final String API_KEY = "api-key";
    public static final String ROLE = "role";
    public static final String VIEWER_ROLE = "viewer";

    Logger logger = LoggerFactory.getLogger(UsersController.class);
    static ObjectMapper objectMapper = new ObjectMapper();
    static String stringPattern = "Bearer\\s([A-Za-z0-9+/]*={0,2})";
    static Pattern pattern = Pattern.compile(stringPattern);

    //this value should come from env variable, adding a default for simplicity
    @Value("${api-key-value:some-api-key}")
    private String apiKeyValue;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String root() {
        return "Greetings from User Management service!";
    }

    @GetMapping("/users")
    public User[] getUsers(@RequestHeader("Authorization") String bearerToken) throws JsonProcessingException {
        logger.info("Here is bearer token: {}", bearerToken);

        Map<String, Object> deserializedTokenPayload = parseToken(bearerToken);
        StringBuffer buf = new StringBuffer();
        deserializedTokenPayload.forEach((k, v) -> buf.append(String.format("%s : %s ;", k, v)));

        logger.info("Decoded token {}", buf.toString()); // should be removed

        // TODO add validation layer

        if (!authenticateRequest((String) deserializedTokenPayload.get(API_KEY))) {
            ErrorResponse errorMessage = new ErrorResponse(ErrorResponse.ErrorMessages.UNAUTHENTICATED.toString());
            try {
                logger.error("Authentication failure!");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, objectMapper.writeValueAsString(errorMessage));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        if (!authorizeRequest((String) deserializedTokenPayload.get(ROLE))) {
            ErrorResponse errorMessage = new ErrorResponse(ErrorResponse.ErrorMessages.FORBIDDEN.toString());
            try {
                logger.error("Authorization failure!");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, objectMapper.writeValueAsString(errorMessage));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Getting users");
        return userService.getUsers();
    }

    private Map<String, Object> parseToken(String bearerToken) throws JsonProcessingException {
        try {
            Matcher m = pattern.matcher(bearerToken);
            if (!m.find())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, objectMapper.writeValueAsString(
                        new ErrorResponse("Bearer token is invalid")));
            String token = m.group(1);
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decodedTokenString = new String(decodedBytes);
            Map<String, Object> deserializedTokenPayload = objectMapper.readValue(decodedTokenString, Map.class);
            return deserializedTokenPayload;
        } catch (JsonProcessingException e) {
            logger.error("Token parsing failed");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, objectMapper.writeValueAsString(
                    new ErrorResponse("Failed to parse bearer token")));
        }
    }

    private boolean authenticateRequest(String receivedApiKeyValue) {
        return apiKeyValue.equals(receivedApiKeyValue);
    }

    private boolean authorizeRequest(String role) {
        return VIEWER_ROLE.equals(role);
    }
}
