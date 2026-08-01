package com.example.contact_list.api.service;

import com.example.contact_list.api.dto.ContactRequest;
import com.example.contact_list.api.dto.ContactResponse;
import com.example.contact_list.api.exception.ResourceConflictException;
import com.example.contact_list.api.exception.ResourceNotFoundException;
import com.example.contact_list.api.mapper.ContactMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.contact_list.api.model.Contact;
import com.example.contact_list.api.repository.ContactRepository;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ContactServiceImpl(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    @Override
    public Page<ContactResponse> getAllContacts(String search, Pageable pageable) {
        Page<Contact> contactPage;
        if (search == null || search.isEmpty()) {
            contactPage = contactRepository.findAll(pageable);
        } else {
            contactPage = contactRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(search, search, pageable);
        }
        return contactPage.map(contactMapper::toResponse);
    }

    @Override
    public ContactResponse getContactById(Integer id) {
        return contactMapper.toResponse(contactRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found")));
    }

    @Override
    @Transactional
    public ContactResponse createContact(ContactRequest contactRequest) {
        validateUniqueEmail(null, contactRequest.getEmail());
        Contact newContact = new Contact();
        contactMapper.toEntity(contactRequest, newContact);
        return contactMapper.toResponse(contactRepository.save(newContact));
    }

    @Override
    @Transactional
    public ContactResponse updateContact(Integer id, ContactRequest contactRequest) {
        Contact updateContact = contactRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        validateUniqueEmail(id, contactRequest.getEmail());
        contactMapper.toEntity(contactRequest, updateContact);
        updateContact.setId(id);
        return contactMapper.toResponse(contactRepository.save(updateContact));
    }

    @Override
    public void deleteContact(Integer id) {
        if (!contactRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contact not found");
        }
        contactRepository.deleteById(id);
    }

    private void validateUniqueEmail(Integer id, String email) {
        if (id == null) {
            if (contactRepository.existsByEmailIgnoreCase(email)) {
                throw new ResourceConflictException("Email contact already exists");
            }
        } else {
            Contact contact = contactRepository.findById(id).orElse(null);
            if (contact != null && !contact.getEmail().equalsIgnoreCase(email)) {
                if (contactRepository.existsByEmailIgnoreCase(email)) {
                    throw new ResourceConflictException("Email contact already exists");
                }
            }
        }
    }
}
