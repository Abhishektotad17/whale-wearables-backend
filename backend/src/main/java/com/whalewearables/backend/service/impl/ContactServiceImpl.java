package com.whalewearables.backend.service.impl;

import com.whalewearables.backend.dto.ContactDto;
import com.whalewearables.backend.model.Contact;
import com.whalewearables.backend.repository.ContactRepository;
import com.whalewearables.backend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    private ContactRepository contactRepository;
    @Override
    public Contact saveContact(ContactDto contactDto) {
        Contact contact = new Contact(
                contactDto.getName(),
                contactDto.getEmail(),
                contactDto.getSubject(),
                contactDto.getMessage()
        );
        return contactRepository.save(contact);
    }
}
