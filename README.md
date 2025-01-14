##

https://spring.io/guides/gs/spring-boot#scratch


## Run / Test

- Using mvn
> ./mvnw spring-boot:run

- Using Docker

> docker build -t haris/user-mgmt-service:v1 .
> docker run -p 8080:8080 haris/user-mgmt-service:v1

## Misc
> curl -H "Authorization: Bearer <ACCESS_TOKEN>" http://localhost:8080/users

> echo -n '{"api-key":"some-api-key", "role":"viewer"}' | base64
> echo -n '{"api-key":"some-api-key", "role":"viewer"}' | base64
> base64 -d <<< SGVsbG8sIFdvcmxkIQo=

### Sample testing commands

```
habdu@Mac user-mgmt-service % echo -n '{"api-key":"some-api-key", "role":"viewer"}' | base64
eyJhcGkta2V5Ijoic29tZS1hcGkta2V5IiwgInJvbGUiOiJ2aWV3ZXIifQ==

curl -H "Authorization: Bearer eyJhcGkta2V5Ijoic29tZS1hcGkta2V5IiwgInJvbGUiOiJ2aWV3ZXIifQ==" http://localhost:8080/users
[{"id":"user1","username":"john.doe"},{"id":"user2","username":"jane.smith"}]%
```

```
habdu@Mac user-mgmt-service % echo -n '{"api-key":"wrong-key", "role":"viewer"}' | base64
eyJhcGkta2V5Ijoid3Jvbmcta2V5IiwgInJvbGUiOiJ2aWV3ZXIifQ==
habdu@Mac user-mgmt-service %
habdu@Mac user-mgmt-service %
habdu@Mac user-mgmt-service % curl -H "Authorization: Bearer eyJhcGkta2V5Ijoid3Jvbmcta2V5IiwgInJvbGUiOiJ2aWV3ZXIifQ==" http://localhost:8080/users
{"timestamp":"2025-01-14T21:22:30.164+00:00","status":401,"error":"Unauthorized","path":"/users"}%
```

```
habdu@Mac user-mgmt-service % echo -n '{"api-key":"some-api-key", "role":"wrong"}' | base64
eyJhcGkta2V5Ijoic29tZS1hcGkta2V5IiwgInJvbGUiOiJ3cm9uZyJ9
habdu@Mac user-mgmt-service %
habdu@Mac user-mgmt-service %
habdu@Mac user-mgmt-service % curl -H "Authorization: Bearer eyJhcGkta2V5Ijoic29tZS1hcGkta2V5IiwgInJvbGUiOiJ3cm9uZyJ9" http://localhost:8080/users
{"timestamp":"2025-01-14T21:23:44.615+00:00","status":403,"error":"Forbidden","path":"/users"}%
```
## Description
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