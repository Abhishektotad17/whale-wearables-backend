package com.whalewearables.backend.service;

import com.whalewearables.backend.dto.ContactDto;
import com.whalewearables.backend.model.Contact;

public interface ContactService {
    Contact saveContact(ContactDto contactDto);
}
