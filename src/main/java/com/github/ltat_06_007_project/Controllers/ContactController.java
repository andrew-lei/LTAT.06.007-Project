package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.Objects.ContactObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactController {

    private final ContactModel contactModel;
    private final ConnectionController connectionController;

    @Autowired
    public ContactController(ContactModel contactModel, ConnectionController connectionController) {
        this.contactModel = contactModel;
        this.connectionController = connectionController;
    }

    public ContactObject addContact(String text) {
        if (!"".equals(text))  {
            connectionController.addConnection(text);
            return contactModel.addContact(new ContactObject(text,new byte[0],new byte[0],"",true));
        }
        return null;
    }

    public List<ContactObject> getAllContacts() {
        return contactModel.getAll()
                .stream()
                .filter(c -> c.getAllowed())
                .collect(Collectors.toList());
    }
}
