##

https://spring.io/guides/gs/spring-boot#scratch


## Run / Test
./mvnw spring-boot:run


> curl -H "Authorization: Bearer <ACCESS_TOKEN>" http://localhost:8080/users

docker build -t haris/user-mgmt-service:v1 .
docker run -p 8080:8080 haris/user-mgmt-service:v1


```
T ask:

1. API Endpoint Design:
Create a REST API endpoint at /users.
This endpoint should (for this exercise) return a hardcoded JSON list of
user objects if authorization is successful. Example:
Unset
[
{"id": "user1", "username": "john.doe"},
{"id": "user2", "username": "jane.smith"}
]

2. Authentication:
Implement a basic authentication mechanism. You can use a simple API
Key authentication model. The API Key will be passed in the
Authorization header with Bearer authentication scheme. Example:
Authorization: Bearer some-api-key.
For this exercise, you can have a hardcoded "valid" API Key value.
If authentication fails (invalid API key), the API should return a 401
Unauthorized status code and a basic error message, such as
{"error": "Invalid API Key"}.

3. Authorization:
Implement basic role-based authorization: For this scenario, assume that
the API endpoint should only be accessed by clients with a viewer role.

Associate roles with API Keys: The some-api-key used above should be
associated with the viewer role, and that role should be checked against
before allowing access. For simplicity, you can assume there are no other
roles for the moment.

If authorization fails (e.g. wrong role), the API should return a 403
Forbidden status code and a basic error message such as {"error":
"Forbidden. Insufficient Permissions"}.
```