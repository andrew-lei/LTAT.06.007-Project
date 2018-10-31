package com.github.ltat_06_007_project.Models;

import com.github.ltat_06_007_project.Objects.ContactObject;
import com.github.ltat_06_007_project.Repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ContactModel {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactModel(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<ContactObject> getAll() {
        try {
            return contactRepository.get();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public boolean addContact(ContactObject contactObject) {
        try {
            contactRepository.insert(contactObject);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Optional<ContactObject> getById(String identificationCode) {
        try {
            return Optional.of(contactRepository.get(identificationCode));
        } catch (SQLException e) {
           return Optional.empty();
        }
    }

    public boolean updateIp(String identificationCode, String ipAddress) {
        try {
            ContactObject oldContact = contactRepository.get(identificationCode);
            ContactObject newContact = new ContactObject(oldContact.getIdCode()
                                                        ,oldContact.getSymmetricKey()
                                                        ,oldContact.getPublicKey()
                                                        ,ipAddress
                                                        ,oldContact.getAllowed());
            contactRepository.update(newContact);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updatePublicKey(String identificationCode, byte[] publicKey) {
        ContactObject oldContact;
        try {
            oldContact = contactRepository.get(identificationCode);
        } catch (SQLException e) {
            oldContact = new ContactObject(identificationCode,new byte[0], publicKey, "",false);
        }
        ContactObject newContact = new ContactObject(oldContact.getIdCode()
                ,oldContact.getSymmetricKey()
                ,publicKey
                ,oldContact.getIpAddress()
                ,oldContact.getAllowed());
        try {
            contactRepository.update(newContact);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
