package com.example.contact_list.api.service;

import java.util.List;

import com.example.contact_list.api.dto.ContactRequest;
import com.example.contact_list.api.dto.ContactResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactService {
    public Page<ContactResponse> getAllContacts(String search, Pageable pageable);

    public ContactResponse getContactById(Integer id);

    public ContactResponse createContact(ContactRequest contact);

    public ContactResponse updateContact(Integer id, ContactRequest contact);

    public void deleteContact(Integer id);
}
