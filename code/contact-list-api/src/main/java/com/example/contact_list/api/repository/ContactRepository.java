package com.example.contact_list.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.contact_list.api.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    Page<Contact> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

    Boolean existsByEmailIgnoreCase(String email);
}
