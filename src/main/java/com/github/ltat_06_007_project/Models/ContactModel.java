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

    public ContactObject addContact(ContactObject contactObject) {
        try {
            ContactObject oldContact = contactRepository.get(contactObject.getIdCode());
            ContactObject newContact = new ContactObject(oldContact.getIdCode()
                    ,oldContact.getSymmetricKey()
                    ,oldContact.getPublicKey()
                    ,oldContact.getIpAddress()
                    ,true);
            contactRepository.update(newContact);
            return newContact;
        } catch (SQLException e) {
            try {
                contactRepository.insert(contactObject);
                return contactObject;
            }
            catch (SQLException e1) {
                return null;
            }
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
                                                        ,oldContact.isAllowed());
            contactRepository.update(newContact);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePublicKey(String identificationCode, byte[] publicKey) {
        try {
            ContactObject oldContact = contactRepository.get(identificationCode);
            ContactObject newContact = new ContactObject(oldContact.getIdCode()
                    ,oldContact.getSymmetricKey()
                    ,publicKey
                    ,oldContact.getIpAddress()
                    ,oldContact.isAllowed());
            contactRepository.update(newContact);
            return true;
        } catch (SQLException e) {
            try {
                contactRepository.insert( new ContactObject(identificationCode,new byte[0], publicKey, "",false));
                return true;
            } catch (SQLException e1) {
                return false;
            }

        }
    }

    public void removeContact(String text) throws SQLException {
        contactRepository.removeByIdCode(text);
    }
}
