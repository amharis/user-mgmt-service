package com.user.mgmt.users;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class ErrorResponse {

    @JsonIgnoreProperties
    public enum ErrorMessages {

        UNAUTHENTICATED("Invalid API Key"),
        FORBIDDEN("Forbidden. Insufficient Permissions");

        private final String errorMessage;

        ErrorMessages(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString() { return errorMessage; }
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    String error;

    public ErrorResponse() {}
    public ErrorResponse(String error) {
        this.error = error;
    }
}
