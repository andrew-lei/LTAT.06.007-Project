package com.github.ltat_06_007_project.Repositories;

import com.github.ltat_06_007_project.Objects.ContactObject;
import com.github.ltat_06_007_project.Objects.MessageObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContactRepository {

    private final String databaseAddress;

    public ContactRepository() {
        try {
            databaseAddress = "jdbc:sqlite:" + (new File(".")).getCanonicalPath() + "/database.db";
            createNewDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNewDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS contact ("
                + " id integer PRIMARY KEY AUTOINCREMENT,"
                + " text identificationCode NOT NULL UNIQUE,"
                + " blob lastKey NOT NULL"
                + " text lastAddress NOT NULL"
                + ");";
        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ContactObject> getAll() {
        String sql = "SELECT * FROM message";

        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);

            List<ContactObject> allContacts = new ArrayList<>();
            while (resultSet.next()) {
                String id = resultSet.getString("identificationCode");
                byte[] key = resultSet.getBytes("lastKey");
                String address = resultSet.getString("lastAddress");
                allContacts.add(new ContactObject(id,key,address));
            }
            return allContacts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
