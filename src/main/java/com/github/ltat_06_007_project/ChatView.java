package com.github.ltat_06_007_project;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class ChatView extends VBox {

    private final TextArea output = new TextArea();
    private final TextField input = new TextField();

    public TextArea getOutput() {
        return output;
    }

    public TextField getInput() {
        return input;
    }

    public ChatView() {
        Platform.runLater(() -> createLayout());
    }

    private void createLayout() {
        getChildren().add(output);
        getChildren().add(input);
    }
}
