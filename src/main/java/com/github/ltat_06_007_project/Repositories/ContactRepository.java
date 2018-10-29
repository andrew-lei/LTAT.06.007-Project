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
                + " identificationCode text NOT NULL UNIQUE,"
                + " lastKey blob NOT NULL,"
                + " lastAddress text NOT NULL"
                + ");";
        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ContactObject> getAll() {
        String sql = "SELECT * FROM contact";

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


    public void updateIp(String idCode, String ipAddress) {
        String sql = "UPDATE contact SET lastaddress = ? WHERE identificationCode = ?";
        try (var connection = DriverManager.getConnection(databaseAddress);
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, ipAddress);
            preparedStatement.setString(2, idCode);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ContactObject getById(String idCode) {
        String sql = "SELECT * FROM contact WHERE identificationCode = ?";

        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);

            resultSet.next();
            String id = resultSet.getString("identificationCode");
            byte[] key = resultSet.getBytes("lastKey");
            String address = resultSet.getString("lastAddress");
            return new ContactObject(id,key,address);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
