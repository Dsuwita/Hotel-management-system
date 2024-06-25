package assignment2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.io.IOException;
import java.net.*;

public class ServerApp extends Application {
    private Hotel hotel = new Hotel("Langara Hotel", "100 W 49th Ave, Vancouver, BC V5Y 2Z6", 10);
    private Scene managementScene;
    private Scene chatScene;
    private TextField chatServiceTextField;
    private TextArea chatServiceTextArea;
    private ComboBox<String> changeClientComboBox;
    private ClientHandler currentClient = null;

    private static final int MAX_CLIENTS = 4;
    private static final int PORT = 1234;

    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hotel management system");

        // Management Scene
        Label guestNameLabel = new Label("Guest name:");
        TextField guestNameField = new TextField();

        Label guestRoomSize = new Label("Room size (1/2):");
        TextField guestRoomSizeField = new TextField();

        Label guestCheckInDateLabel = new Label("Check in date:");
        TextField guestCheckInDateField = new TextField();

        Label guestCheckOutDateLabel = new Label("Check out date:");
        TextField guestCheckOutDateField = new TextField();

        TextArea guestList = new TextArea();
        guestList.setEditable(false);
        guestList.setText(hotel.toString());

        ComboBox<Integer> roomNumberComboBox = new ComboBox<Integer>();
        roomNumberComboBox.setPrefWidth(110);

        for (Guest guest : hotel.getGuests()) {
            roomNumberComboBox.getItems().add(guest.getRoom().getRoomNumber() + 1);
        }

        Button addGuestButton = new Button("Add guest");
        addGuestButton.setPrefWidth(240);
        addGuestButton.setOnAction(e -> {
            if (guestNameField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Guest name cannot be empty");
                alert.showAndWait();
                return;
            }

            if (guestRoomSizeField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Room size cannot be empty");
                alert.showAndWait();
                return;
            } else if (Integer.parseInt(guestRoomSizeField.getText()) != 1
                    && Integer.parseInt(guestRoomSizeField.getText()) != 2) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Room size must be 1 or 2");
                alert.showAndWait();
                return;
            }

            if (guestCheckInDateField.getText().equals("") && !guestCheckOutDateField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Check in date cannot be empty if check out date is present");
                alert.showAndWait();
                return;
            } else if (!guestCheckInDateField.getText().equals("") && guestCheckOutDateField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Check out date cannot be empty if check in date is present");
                alert.showAndWait();
                return;
            }

            String guestName = guestNameField.getText();
            int roomSize = Integer.parseInt(guestRoomSizeField.getText());
            String guestCheckInDate = guestCheckInDateField.getText();
            String guestCheckOutDate = guestCheckOutDateField.getText();

            if (guestCheckInDate.equals("") && guestCheckOutDate.equals("")) {
                roomNumberComboBox.getItems().add(hotel.addGuest(guestName, roomSize));
            } else {
                roomNumberComboBox.getItems().add(hotel.addGuest(guestName, roomSize, guestCheckInDate,
                        guestCheckOutDate));
            }

            guestList.setText(hotel.toString());
            hotel.writeToFile();
        });

        Button removeGuestButton = new Button("Remove guest");
        removeGuestButton.setPrefWidth(110);
        removeGuestButton.setOnAction(e -> {
            if (roomNumberComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a room number");
                alert.showAndWait();
                return;
            }

            hotel.removeGuest(roomNumberComboBox.getValue() - 1);

            guestList.setText(hotel.toString());

            roomNumberComboBox.getItems().remove(roomNumberComboBox.getValue());
            hotel.writeToFile();
        });

        Button switchToChat = new Button("Switch to chat");
        switchToChat.setPrefWidth(240);
        switchToChat.setOnAction(e -> {
            primaryStage.setScene(chatScene);
        });

        GridPane addGuestGrid = new GridPane();
        addGuestGrid.add(guestNameLabel, 0, 0);
        addGuestGrid.add(guestNameField, 1, 0);
        addGuestGrid.add(guestRoomSize, 0, 1);
        addGuestGrid.add(guestRoomSizeField, 1, 1);
        addGuestGrid.add(guestCheckInDateLabel, 0, 2);
        addGuestGrid.add(guestCheckInDateField, 1, 2);
        addGuestGrid.add(guestCheckOutDateLabel, 0, 3);
        addGuestGrid.add(guestCheckOutDateField, 1, 3);

        HBox removeGuestButtonLayout = new HBox(20);
        removeGuestButtonLayout.getChildren().addAll(roomNumberComboBox, removeGuestButton);

        VBox addGuestLayout = new VBox(20);
        addGuestLayout.getChildren().addAll(addGuestGrid, addGuestButton, removeGuestButtonLayout, switchToChat);

        HBox addGuestSceneLayout = new HBox(20);
        addGuestSceneLayout.getChildren().addAll(addGuestLayout, guestList);

        managementScene = new Scene(addGuestSceneLayout, 800, 600);

        // Chat service scene
        Label chatServiceLabel = new Label("Chat service");
        chatServiceTextArea = new TextArea();
        chatServiceTextArea.setEditable(false);
        chatServiceTextArea.setPrefHeight(500);

        chatServiceTextField = new TextField();
        chatServiceTextField.setPrefWidth(500);
        chatServiceTextField.setPrefHeight(50);
        chatServiceTextField.setOnAction(e -> {
            if (chatServiceTextField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Message cannot be empty");
                alert.showAndWait();
                return;
            }

            sendMessage(chatServiceTextField.getText());
            chatServiceTextField.setText("");
        });

        changeClientComboBox = new ComboBox<>();
        changeClientComboBox.setPrefWidth(100);

        Button switchToManagement = new Button("Switch to management");
        switchToManagement.setPrefWidth(600);
        switchToManagement.setOnAction(e -> {
            primaryStage.setScene(managementScene);
        });

        Button changeClientButton = new Button("Change client");
        changeClientButton.setPrefWidth(100);
        changeClientButton.setOnAction(e -> {
            if (changeClientComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a client");
                alert.showAndWait();
                return;
            }

            chatServiceTextArea.clear();

            currentClient.setCurrentlyViewed(false);
            currentClient = clients.get(Integer.parseInt(changeClientComboBox.getValue()) - 1);

            chatServiceTextArea.setText(currentClient.getChatHistory());
            currentClient.setCurrentlyViewed(true);

        });

        Button terminateClientButton = new Button("Terminate current client");
        terminateClientButton.setPrefWidth(200);
        terminateClientButton.setOnAction(e -> {
            if (currentClient == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No client is currently connected");
                alert.showAndWait();
                return;
            }

            try {
                closeCurrentClient();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        new Thread(this::startServer).start();

        HBox changeClientLayout = new HBox(20);
        changeClientLayout.getChildren().addAll(changeClientComboBox, changeClientButton);
        changeClientLayout.setAlignment(Pos.CENTER);

        VBox chatServiceSceneLayout = new VBox(20);
        chatServiceSceneLayout.getChildren().addAll(chatServiceLabel, changeClientLayout, terminateClientButton,
                chatServiceTextArea, chatServiceTextField, switchToManagement);
        chatServiceSceneLayout.setAlignment(Pos.CENTER);

        chatScene = new Scene(chatServiceSceneLayout, 600, 600);

        primaryStage.setScene(managementScene);
        primaryStage.show();
    }

    public void startServer() {
        try {
            ServerSocket server = new ServerSocket(PORT);

            while (true) {
                Socket socket = server.accept();
                SocketComm socketComm = new SocketComm(socket);

                if (clients.size() > MAX_CLIENTS) {
                    sendMessage("The server is full");
                    socketComm.close();
                    continue;
                }

                ClientHandler clientThread = new ClientHandler(socketComm, chatServiceTextArea, clients.size() + 1);
                clients.add(clientThread);

                new Thread(clientThread).start();

                if (currentClient == null) {
                    currentClient = clientThread;
                    currentClient.setCurrentlyViewed(true);
                    chatServiceTextArea.setText(currentClient.getChatHistory());
                }

                Platform.runLater(() -> {
                    changeClientComboBox.getItems().add("" + (clients.indexOf(clientThread) + 1));
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (currentClient != null) {
            currentClient.sendMessage(message);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Client Connected");
            alert.setHeaderText("No one hears you");
            alert.showAndWait();
            return;
        }
    }

    public void closeCurrentClient() throws IOException {
        if (currentClient != null) {
            changeClientComboBox.getItems().remove("" + (clients.indexOf(currentClient) + 1));
            currentClient.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Client Selected");
            alert.setHeaderText("Please select a client to terminate");
            alert.showAndWait();
            return;
        }
    }

    public static void main(String[] args) {
        launch();
    }

}