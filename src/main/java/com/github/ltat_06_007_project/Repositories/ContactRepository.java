package com.github.ltat_06_007_project.Repositories;

import com.github.ltat_06_007_project.Objects.ContactObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "identificationCode TEXT NOT NULL UNIQUE,"
                + "symmetricKey BLOB,"
                + "publicKey BLOB,"
                + "ipAddress TEXT,"
                + "allowed INTEGER NOT NULL"
                + ");";

        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(ContactObject contactObject) throws SQLException {
        String sql = "INSERT INTO contact(identificationCode,symmetricKey,publicKey,ipAddress,allowed)" +
                " VALUES(?,?,?,?,?)";
        try (var connection = DriverManager.getConnection(databaseAddress);
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, contactObject.getIdCode());
            preparedStatement.setBytes(2, contactObject.getSymmetricKey());
            preparedStatement.setBytes(3, contactObject.getPublicKey());
            preparedStatement.setString(4, contactObject.getIpAddress());
            preparedStatement.setInt(5, contactObject.getAllowed() ? 1 : 0);
            preparedStatement.executeUpdate();
        }

    }

    public void update(ContactObject contactObject) throws SQLException {
        String sql = "UPDATE contact " +
                "SET symmetricKey = ?, ipAddress = ?, publicKey = ?, allowed = ?" +
                "WHERE identificationCode = ?";
        try (var connection = DriverManager.getConnection(databaseAddress);
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBytes(1, contactObject.getSymmetricKey());
            preparedStatement.setString(2, contactObject.getIpAddress());
            preparedStatement.setBytes(3, contactObject.getPublicKey());
            preparedStatement.setInt(4, contactObject.getAllowed() ? 1 : 0);
            preparedStatement.setString(5, contactObject.getIdCode());
            preparedStatement.executeUpdate();
        }
    }

    public List<ContactObject> get() throws SQLException {
        String sql = "SELECT * FROM contact";

        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery(sql)) {

            List<ContactObject> allContacts = new ArrayList<>();
            while (resultSet.next()) {
                allContacts.add(createObject(resultSet));
            }
            return allContacts;
        }
    }

    public ContactObject get(String identificationCode) throws SQLException {
        String sql = "SELECT * FROM contact WHERE identificationCode = " + identificationCode;

        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return createObject(resultSet);
        }
    }

    private ContactObject createObject(ResultSet resultSet) throws SQLException {
        String identificationCode = resultSet.getString("identificationCode");
        byte[] symmetricKey = resultSet.getBytes("symmetricKey");
        String ipAddress = resultSet.getString("ipAddress");
        byte[] publicKey = resultSet.getBytes("publicKey");
        boolean allowed = resultSet.getInt("allowed") != 0;
        return new ContactObject(identificationCode, symmetricKey, publicKey, ipAddress, allowed);
    }
}
