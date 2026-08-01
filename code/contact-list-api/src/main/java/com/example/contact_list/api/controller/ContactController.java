package com.example.contact_list.api.controller;

import com.example.contact_list.api.dto.ApiErrorResponse;
import com.example.contact_list.api.dto.ContactRequest;
import com.example.contact_list.api.dto.ContactResponse;
import com.example.contact_list.api.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Tag(name = "Contacts", description = "Operations related to Contacts")
public class ContactController {
    private final ContactService contactService;
    private final PagedResourcesAssembler<ContactResponse> pagedResourcesAssembler;

    @Operation(summary = "Get all contacts", description = "Returns a list of all registered contacts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List successfully returned",
                    content = @Content(
                            mediaType = "application/hal+json",
                            schema = @Schema(implementation = PagedModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ContactResponse>>> getAllContacts(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<ContactResponse> contactPage = contactService.getAllContacts(search, pageable);
        PagedModel<EntityModel<ContactResponse>> pagedModel = pagedResourcesAssembler
                .toModel(contactPage, this::entityWithLinks);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Get a contact by ID", description = "Returns the details of a specific contact using its ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contact successfully found",
                    content = @Content(
                            mediaType = "application/hal+json",
                            schema = @Schema(implementation = ContactResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ContactResponse>> getContactById(@PathVariable Integer id) {
        ContactResponse contact = contactService.getContactById(id);
        return ResponseEntity.ok(entityWithLinks(contact));
    }

    @Operation(summary = "Create a new contact", description = "Create a new contact")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Contact successfully created",
                    content = @Content(
                            mediaType = "application/hal+json",
                            schema = @Schema(implementation = ContactResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<ContactResponse>> createContact(@Valid @RequestBody ContactRequest contactRequest) {
        ContactResponse contact = contactService.createContact(contactRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(entityWithLinks(contact));
    }

    @Operation(summary = "Update a contact by ID", description = "Returns the details of the updated contact")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contact successfully updated",
                    content = @Content(
                            mediaType = "application/hal+json",
                            schema = @Schema(implementation = ContactResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ContactResponse>> updateContact(@PathVariable Integer id,
                                                                      @Valid @RequestBody ContactRequest contactRequest) {
        ContactResponse contact = contactService.updateContact(id, contactRequest);
        return ResponseEntity.ok(entityWithLinks(contact));
    }

    @Operation(summary = "Delete a contact", description = "Delete a contact using its ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Contact successfully deleted",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contact not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Integer id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ContactResponse> entityWithLinks(ContactResponse contact) {
        Link selfLink = linkTo(methodOn(ContactController.class)
                .getContactById(contact.getId()))
                .withSelfRel();
        Link updateLink = linkTo(methodOn(ContactController.class)
                .updateContact(contact.getId(), null))
                .withRel("update");
        Link deleteLink = linkTo(methodOn(ContactController.class)
                .deleteContact(contact.getId()))
                .withRel("delete");
        return EntityModel.of(contact, selfLink, updateLink, deleteLink);
    }
}
