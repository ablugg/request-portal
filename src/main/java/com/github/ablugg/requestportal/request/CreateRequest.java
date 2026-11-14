package com.github.ablugg.requestportal.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The JSON body a customer sends to submit a request. A record is an immutable data
 * carrier: Java generates the constructor and accessors. The annotations are checked
 * before the controller method runs, and the messages go back to the caller on failure.
 */
public record CreateRequest(

        @NotNull(message = "Choose a category")
        RequestCategory category,

        @NotBlank(message = "Enter a short title")
        @Size(max = 120, message = "Title must be 120 characters or fewer")
        String title,

        @NotBlank(message = "Describe the request")
        @Size(max = 2000, message = "Description must be 2000 characters or fewer")
        String description,

        @NotBlank(message = "Enter a contact email")
        @Email(message = "Enter a valid email address")
        @Size(max = 254, message = "Email must be 254 characters or fewer")
        String contactEmail) {
}
