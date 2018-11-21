package com.github.ltat_06_007_project.Repositories;

import com.github.ltat_06_007_project.Objects.MessageObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
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
                + "senderId text NOT NULL,"
                + "receiverId text NOT NULL,"
                + "messageSentTime INT NOT NULL"
                + ");";
        try (Connection connection = DriverManager.getConnection(databaseAddress);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageObject insertMessage(MessageObject messageObject) {
        String sql = "INSERT INTO message(content,senderId, receiverId, messageSentTime) VALUES(?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(databaseAddress);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, messageObject.getContent());
            preparedStatement.setString(2, messageObject.getSenderId());
            preparedStatement.setString(3, messageObject.getReceiverId());
            preparedStatement.setLong(4, messageObject.getMessageSentTime().getTime());
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
                allMessageObjects.add(new MessageObject(resultSet.getString("content"),resultSet.getString("senderId"), resultSet.getString("receiverId"), new Date(resultSet.getLong("messageSentTime"))));
            }
            return allMessageObjects;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
