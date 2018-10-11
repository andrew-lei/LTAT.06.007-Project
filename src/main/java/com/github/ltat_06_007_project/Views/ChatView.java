package com.github.ltat_06_007_project.Views;

import com.github.ltat_06_007_project.Controllers.ChatController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ChatView extends GridPane {

    private ChatController chatController;

    private ScrollPane outputContainer;
    private VBox outputPane;
    private HBox inputContainer;
    private TextField input;
    private ScrollPane contactContainer;
    private Button sendButton;



    @Autowired
    public ChatView(ChatController chatController) {
        this.chatController = chatController;
        this.setPrefSize(640, 480);
        this.setStyle(" -fx-background-color: white; -fx-border-width: 0");
        initComponents();
        Platform.runLater(this::createLayout);
    }

    private void createLayout() {
        this.setHgap(10);
        this.setVgap(10);
        this.add(outputContainer, 1, 0);
        this.add(contactContainer, 0, 0, 1, 2);
        this.add(inputContainer, 1, 1);

    }
    private void initComponents(){
        createSendMessageButton();
        createOutputArea();
        createInputPane();
        createContactPane( );
    }
    private void createContactPane(){
        contactContainer = new ScrollPane();
        var contactPane = new VBox();
        contactContainer.setMinWidth(150);
        var contactsLabel = new Label("Contacts");
        contactsLabel.setStyle("-fx-background-color: white; -fx-font-size: 16;");
        contactsLabel.setPrefWidth(150);
        contactsLabel.setAlignment(Pos.TOP_CENTER);
        contactPane.prefHeightProperty().bind(contactContainer.heightProperty());
        contactPane.getChildren().add(contactsLabel);
        contactPane.setStyle(" -fx-background-color: white; -fx-border-width: 0");
        contactContainer.setStyle(" -fx-background-color: white; -fx-border-width: 0");
        contactContainer.setContent(contactPane);
    }

    private void createOutputArea(){
        outputPane = new VBox();
        loadOutput();

        outputContainer = new ScrollPane();
        outputContainer.prefHeightProperty().bind(this.heightProperty().subtract(60));
        outputContainer.prefWidthProperty().bind(this.widthProperty().subtract(150));
        outputPane.setSpacing(5);
        outputPane.setPadding(new Insets(0, 0, 10, 0));
        outputPane.setAlignment(Pos.CENTER);
        outputPane.prefWidthProperty().bind(outputContainer.widthProperty().subtract(20));
        outputPane.prefHeightProperty().bind(outputContainer.heightProperty().subtract(40));
        outputPane.setStyle("-fx-background-color: white; -fx-border-width: 0");
        outputContainer.setVbarPolicy( ScrollPane.ScrollBarPolicy.AS_NEEDED);
        outputContainer.vvalueProperty().bind(outputPane.heightProperty().add(10));
        outputContainer.setStyle(" -fx-border-width: 0");
        outputContainer.setContent(outputPane);
    }
    private void createSendMessageButton(){
        sendButton = new Button();
        sendButton.setText("Send");
        sendButton.setOnAction(e -> sendMessage());
        sendButton.setStyle("-fx-background-color: #0099ff; -fx-text-fill: white;");
        sendButton.setAlignment(Pos.CENTER);
        sendButton.setPrefSize(50, 20);
    }
    private void createInputPane(){
        inputContainer = new HBox();
        inputContainer.setSpacing(10);
        input = new TextField();
        input.setOnAction(e -> sendMessage());
        HBox.setHgrow(input, Priority.ALWAYS);
        inputContainer.setMaxHeight(50);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setPadding(new Insets(0, 10, 10, 0));
        inputContainer.getChildren().addAll(input, sendButton);
    }
    private void sendMessage(){
        var message = chatController.addMessage(input.getText());
        input.clear();
        var text = new Text(message.getContent());
        text.setFill(Color.WHITE);
        var textBox = new TextFlow(text);
        textBox.setStyle("-fx-background-color: #0099ff; -fx-border-radius: 5; -fx-background-radius:5; -fx-padding: 5; -fx-end-margin: 10");
        textBox.setTextAlignment(TextAlignment.RIGHT);
        outputPane.setAlignment(Pos.BASELINE_RIGHT);

        outputPane.getChildren().add(textBox);
    }
    private void loadOutput(){
        chatController.getAllMessages().forEach(m -> {
            var message = new Text(m.getContent());
            message.setFill(Color.WHITE);
            var textBox = new TextFlow(message);
            textBox.setTextAlignment(TextAlignment.LEFT);
            textBox.minWidthProperty().bind(outputPane.widthProperty().subtract(10));
            outputPane.setAlignment(Pos.BASELINE_LEFT);
            textBox.setStyle("-fx-background-color: #0099ff; -fx-border-radius: 5; -fx-background-radius:5; -fx-padding: 5");
            outputPane.getChildren().add(textBox);
        });
    }
}
