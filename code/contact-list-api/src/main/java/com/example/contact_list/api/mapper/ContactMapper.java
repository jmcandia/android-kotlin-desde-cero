package com.example.contact_list.api.mapper;

import com.example.contact_list.api.dto.ContactRequest;
import com.example.contact_list.api.dto.ContactResponse;
import com.example.contact_list.api.model.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    ContactResponse toResponse(Contact contact);

    List<ContactResponse> toResponseList(List<Contact> contacts);

    @Mapping(target = "id", ignore = true)
    void toEntity(ContactRequest contactRequest, @MappingTarget Contact contact);
}
