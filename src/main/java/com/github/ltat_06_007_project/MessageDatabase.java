package com.github.ltat_06_007_project;

import com.github.ltat_06_007_project.Objects.MessageObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageDatabase {

    private final String databaseAddress;

    public MessageDatabase() {
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
                + " content text NOT NULL"
                + ");";
        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageObject insertMessage(MessageObject messageObject) {
        String sql = "INSERT INTO message(content) VALUES(?)";

        try (var connection = DriverManager.getConnection(databaseAddress);
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, messageObject.getContent());
            preparedStatement.executeUpdate();
            return messageObject;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MessageObject> getAllMessages() {
        String sql = "SELECT * FROM message";

        try (var connection = DriverManager.getConnection(databaseAddress);
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);

            List<MessageObject> allMessageObjects = new ArrayList<>();
            while (resultSet.next()) {
                allMessageObjects.add(new MessageObject(resultSet.getString("content")));
            }
            return allMessageObjects;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
