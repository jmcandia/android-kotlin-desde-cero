package com.example.contact_list.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ContactRequest {

    @Schema(description = "First name", example = "Anthony")
    @NotBlank
    @Length(min = 3, max = 150)
    private String firstName;

    @Schema(description = "Last name", example = "Stark")
    @NotBlank
    @Length(min = 3, max = 150)
    private String lastName;

    @Schema(description = "Email address", example = "i.am.ironman@example.com")
    @NotBlank
    @Email
    @Length(min = 3, max = 255)
    private String email;

    @Schema(description = "Cell phone number", example = "212-970-4133")
    @Length(max = 20)
    private String phone;

    @Schema(description = "Address", example = "10880 Malibu Point")
    @Length(max = 255)
    private String address;

    @Schema(description = "City", example = "Malibu")
    @Length(max = 100)
    private String city;
}
