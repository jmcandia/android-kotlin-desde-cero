package com.example.contact_list.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactResponse extends RepresentationModel<ContactResponse> {

    @Schema(description = "Database ID", example = "1")
    private Integer id;

    @Schema(description = "First name", example = "Anthony")
    private String firstName;

    @Schema(description = "Last name", example = "Stark")
    private String lastName;

    @Schema(description = "Email address", example = "i.am.ironman@example.com")
    private String email;

    @Schema(description = "Cell phone number", example = "212-970-4133")
    private String phone;

    @Schema(description = "Address", example = "10880 Malibu Point")
    private String address;

    @Schema(description = "City", example = "Malibu")
    private String city;
}
