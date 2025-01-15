# User Management Service

This is Java application built using Spring boot.
The application exposes two GET endpoints
- 
- root endpoint for debugging
````
habdu@Mac user-mgmt-service % curl  http://localhost:8080/
Greetings from User Management service!%
````

- users endpoint, expects a custom authorization header payload containing apikey and role properties.
- a hard coded apikey is used for the purpose of this task -> "some-api-key"
- the api expects a base64 encoded json payload as shown below

```
habdu@Mac user-mgmt-service % echo -n '{"apikey":"some-api-key", "role":"viewer"}' | base64
eyJhcGlrZXkiOiJzb21lLWFwaS1rZXkiLCAicm9sZSI6InZpZXdlciJ9

habdu@Mac user-mgmt-service % curl -H "Authorization: Bearer eyJhcGlrZXkiOiJzb21lLWFwaS1rZXkiLCAicm9sZSI6InZpZXdlciJ9" http://localhost:8080/users
[{"id":"user1","username":"john.doe"},{"id":"user2","username":"jane.smith"}]%

```
## Important considerations

The authentication task sets out a guideline to use authorization header for passing api key. However, that
has consequences on adding authorization functionality.
- whether keep using authorization header for only authentication payload i-e an apikey, and pass authorization
data using other means like query param, request body etc
- or use authorization header to carry additional payload than just apikey


In context of OIDC and OAuth, access and id-tokens are used for passing authentication and authorization payloads.
I have chosen to follow that approach and created a custom token payload inspired by above. I feel that is more
inline with best practices

## Run / Test

- Using mvn
> ./mvnw spring-boot:run

- Using Docker
```
docker build -t haris/user-mgmt-service:v1 .
docker run -p 8080:8080 haris/user-mgmt-service:v1
```

## Sample testing commands

```
habdu@Mac user-mgmt-service % echo -n '{"apikey":"some-api-key", "role":"viewer"}' | base64
eyJhcGlrZXkiOiJzb21lLWFwaS1rZXkiLCAicm9sZSI6InZpZXdlciJ9

curl -H "Authorization: Bearer eyJhcGlrZXkiOiJzb21lLWFwaS1rZXkiLCAicm9sZSI6InZpZXdlciJ9" http://localhost:8080/users
[{"id":"user1","username":"john.doe"},{"id":"user2","username":"jane.smith"}]%
```

```
habdu@Mac user-mgmt-service % echo -n '{"apikey":"wrong-key", "role":"viewer"}' | base64
eyJhcGlrZXkiOiJ3cm9uZy1rZXkiLCAicm9sZSI6InZpZXdlciJ9
habdu@Mac user-mgmt-service %
habdu@Mac user-mgmt-service % curl -H "Authorization: Bearer eyJhcGlrZXkiOiJ3cm9uZy1rZXkiLCAicm9sZSI6InZpZXdlciJ9" http://localhost:8080/users
Invalid API Key%
```

```
habdu@Mac user-mgmt-service % echo -n '{"apikey":"some-api-key", "role":"wrong"}' | base64
eyJhcGlrZXkiOiJzb21lLWFwaS1rZXkiLCAicm9sZSI6Indyb25nIn0=
habdu@Mac user-mgmt-service %
habdu@Mac user-mgmt-service % curl  -H  "Authorization: Bearer eyJhcGlrZXkiOiJzb21lLWFwaS1rZXkiLCAicm9sZSI6Indyb25nIn0=" http://localhost:8080/users
Forbidden, Insufficient Permissions%
```

## Misc
> curl -H "Authorization: Bearer <ACCESS_TOKEN>" http://localhost:8080/users

> echo -n '{"api-key":"some-api-key", "role":"viewer"}' | base64
> echo -n '{"api-key":"some-api-key", "role":"viewer"}' | base64
> base64 -d <<< SGVsbG8sIFdvcmxkIQo=

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