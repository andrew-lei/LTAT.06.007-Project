package com.github.ltat_06_007_project.Repositories;

import com.github.ltat_06_007_project.Controllers.ConnectionController;
import com.github.ltat_06_007_project.Objects.MessageObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageRepository {

    private final String databaseAddress;

    public MessageRepository() {
        try {
            databaseAddress = "jdbc:sqlite:" + (new File(".")).getCanonicalPath() + "/database.db";
            createNewDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNewDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS message ("
                + " id integer PRIMARY KEY AUTOINCREMENT,"
                + " content text NOT NULL,"
                + "contactId text NOT NULL"
                + ");";
        try (Connection connection = DriverManager.getConnection(databaseAddress);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageObject insertMessage(MessageObject messageObject) {
        String sql = "INSERT INTO message(content,contactId) VALUES(?,?)";

        try (Connection connection = DriverManager.getConnection(databaseAddress);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, messageObject.getContent());
            preparedStatement.setString(2, messageObject.getContactId());
            preparedStatement.executeUpdate();
            return messageObject;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MessageObject> getAllMessages(){
        String sql = "SELECT * FROM message";

        try (Connection connection = DriverManager.getConnection(databaseAddress);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            List<MessageObject> allMessageObjects = new ArrayList<>();
            while (resultSet.next()) {
                allMessageObjects.add(new MessageObject(resultSet.getString("content"),resultSet.getString("contactId")));
            }
            return allMessageObjects;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
