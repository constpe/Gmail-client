package mail_client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.regex.*;

public class Client extends Application{
    String address = "";
    String password = "";
    MailMessage message = new MailMessage();
    TextArea messagesArea;

    class Receiver implements Runnable{
        Receiver() {
            Thread t = new Thread(this);
            t.start();
        }

        @Override
        public void run() {
            String result = "";
            try {
                while (true) {
                    if (!(result = message.receive(address, password)).equals("")) {
                        messagesArea.setText(result);
                    }
                }
            }
            catch (Exception e) {}
        }
    }

    public void log() {
        TextField addressField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button logButton = new Button("Log in");

        VBox logBox = new VBox();
        logBox.setSpacing(5);
        logBox.setPadding(new Insets(10));
        logBox.getChildren().add(new Label("Enter your email address"));
        logBox.getChildren().add(addressField);
        logBox.getChildren().add(new Label("Enter your email password"));
        logBox.getChildren().add(passwordField);
        logBox.getChildren().add(logButton);

        Scene logScene = new Scene(logBox, 250, 150);
        Stage log = new Stage();
        log.setTitle("Log in");
        log.initStyle(StageStyle.UTILITY);
        log.setResizable(false);
        log.setScene(logScene);
        log.initModality(Modality.APPLICATION_MODAL);

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+@gmail.com$");

        logButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!(addressField.getText().equals("") || passwordField.getText().equals(""))) {
                    Matcher matcher = pattern.matcher(addressField.getText());
                    if (matcher.matches()) {
                        address = addressField.getText();
                        password = passwordField.getText();
                        log.close();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "You've entered incorrect email");
                        alert.show();
                    }
                }
            }
        });

        log.showAndWait();
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Lab 3 - gmail client");

        Pane rootNode = new Pane();

        Label messagesLabel = new Label("New messages");

        messagesArea = new TextArea();
        messagesArea.setPrefWidth(590);
        messagesArea.setPrefHeight(300);
        messagesArea.setEditable(false);
        messagesArea.setWrapText(true);

        Label toLabel = new Label("To");

        TextField toField = new TextField();

        Label subjectLabel = new Label("Subject");

        TextField subjectField = new TextField();

        Label letterLabel = new Label("Your message");

        TextArea letterArea = new TextArea();
        letterArea.setPrefWidth(590);
        letterArea.setPrefHeight(100);
        letterArea.setWrapText(true);

        Button sendButton = new Button("Send");
        sendButton.setPrefWidth(150);

        Button logButton = new Button("Log in");
        logButton.setPrefWidth(150);

        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(290);
        buttonsBox.getChildren().add(sendButton);
        buttonsBox.getChildren().add(logButton);

        Label addressLabel = new Label("No current email address");

        VBox messagesVBox = new VBox();
        messagesVBox.setSpacing(7);
        messagesVBox.setPadding(new Insets(10));
        messagesVBox.getChildren().add(messagesLabel);
        messagesVBox.getChildren().add(messagesArea);
        messagesVBox.getChildren().add(toLabel);
        messagesVBox.getChildren().add(toField);
        messagesVBox.getChildren().add(subjectLabel);
        messagesVBox.getChildren().add(subjectField);
        messagesVBox.getChildren().add(letterLabel);
        messagesVBox.getChildren().add(letterArea);
        messagesVBox.getChildren().add(buttonsBox);
        messagesVBox.getChildren().add(addressLabel);

        rootNode.getChildren().add(messagesVBox);

        sendButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (message.send(address, password, toField.getText(), subjectField.getText(), letterArea.getText())) {
                    case Constants.NO_ERROR:
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Message was succesfully sent");
                        alert.show();
                        letterArea.clear();
                        break;
                    case Constants.LOGGING_ERROR:
                        alert = new Alert(Alert.AlertType.ERROR, "Log in failed");
                        alert.show();
                        break;
                    case Constants.MESSAGING_ERROR:
                        alert = new Alert(Alert.AlertType.ERROR, "Messaging failed");
                        alert.show();
                        break;
                    case Constants.ERROR:
                        alert = new Alert(Alert.AlertType.ERROR, "Some error happened during sending. Sending failed");
                        alert.show();
                        break;
                }
            }
        });

        logButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                log();
                if (!address.equals("")) {
                    addressLabel.setText("Current email address: " + address);
                    new Receiver();
                }
            }
        });

        Scene mainScene = new Scene(rootNode, 600, 630);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
