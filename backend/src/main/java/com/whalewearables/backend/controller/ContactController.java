package com.whalewearables.backend.controller;

import com.whalewearables.backend.dto.ContactDto;
import com.whalewearables.backend.model.Contact;
import com.whalewearables.backend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody ContactDto contactDto) {
        Contact savedContact = contactService.saveContact(contactDto);
        return ResponseEntity.ok(savedContact);
    }
}
