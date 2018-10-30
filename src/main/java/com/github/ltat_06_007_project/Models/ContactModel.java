package com.github.ltat_06_007_project.Models;

import com.github.ltat_06_007_project.Objects.ContactObject;
import com.github.ltat_06_007_project.Repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContactModel {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactModel(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<ContactObject> getAll() {
        return contactRepository.getAll();
    }

    public void addContact(String idCode) {
        contactRepository.insert(new ContactObject(idCode,new byte[0], ""));
    }

    public ContactObject getById(String idCode) {
        return contactRepository.getById(idCode);
    }

    public void updateIp(String idCode, String ipAddress) {
        contactRepository.updateIp(idCode, ipAddress);
    }

    public void updatePublicKey(byte[] publicKey, String id) {
        contactRepository.updatePublicKey(id, publicKey);
    }
}
