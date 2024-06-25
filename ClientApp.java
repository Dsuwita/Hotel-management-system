package assignment2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientApp extends Application {
    private TextArea chatArea;
    private TextField inputArea;
    private SocketComm socketComm;
    private Socket clientSocket;
    private String ipaddr = "127.0.0.1";
    private boolean talkingToServer = false;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws UnknownHostException, IOException {
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(400);
        chatArea.setPrefWidth(300);
        chatArea.setStyle("-fx-font-size: 15");

        inputArea = new TextField();
        inputArea.setEditable(true);
        inputArea.setPrefHeight(50);
        inputArea.setOnAction(e -> sendMessage());

        Button connectButton = new Button("Connect");
        connectButton.setPrefWidth(600);
        connectButton.setOnAction(e -> {
            new Thread(() -> {
                try {
                    startClient();
                } catch (Exception e1) {
                    chatArea.clear();
                    Platform.runLater(() -> chatArea.appendText("Server is not available.\n"));
                }
            }).start();
        });

        VBox root = new VBox(connectButton, chatArea, inputArea);
        Scene scene = new Scene(root, 600, 500);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Client");
        primaryStage.show();

    }

    private void startClient() throws UnknownHostException, IOException {
        clientSocket = new Socket(ipaddr, SocketComm.PORT);
        talkingToServer = true;

        try {
            socketComm = new SocketComm(clientSocket);
            Platform.runLater(() -> chatArea.appendText("Server is currently occupied. Please wait...\n"));

            while (socketComm.getDataFromClient() == null) {
                // wait for server to respond
            }

            chatArea.clear();
            Platform.runLater(() -> chatArea.appendText("Connected to server: " + clientSocket + "\n"));
            Platform.runLater(() -> chatArea.appendText("To leave, type: " + SocketComm.BYE + "\n"));

            String dataFromServer;
            while (true) {
                dataFromServer = socketComm.getDataFromClient();
                String message = dataFromServer;

                if (message.equals(SocketComm.BYE) || message.equals(null)) {
                    socketComm.sendDataToClient(SocketComm.BYE);
                    Platform.runLater(() -> chatArea.appendText("Customer Service: " + message + "\n"));
                    break;
                }

                Platform.runLater(() -> chatArea.appendText("Customer Service: " + message + "\n"));
            }

            Platform.runLater(() -> chatArea.appendText("You have disconnected.\n"));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private void sendMessage() {

        if (talkingToServer) {
            String response = inputArea.getText();
            socketComm.sendDataToClient(response);
            Platform.runLater(() -> chatArea.appendText("You: " + response + "\n"));
            inputArea.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Not Connected to server ");
            alert.setHeaderText("NOT CONNECTED TO SERVER, PLEASE CONNECT");
            alert.showAndWait();
            return;

        }
    }
}