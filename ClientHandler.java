package assignment2;

import java.io.IOException;
import java.net.Socket;

import javafx.scene.control.*;

public class ClientHandler implements Runnable {
    private SocketComm socketComm;
    private TextArea textArea;
    private String chatHistory;
    private Boolean currentlyViewed;

    public ClientHandler(SocketComm socketComm, TextArea textArea, int index) {
        this.socketComm = socketComm;
        this.textArea = textArea;
        this.chatHistory = "You are currently talking to client " + index + "\n";
        this.currentlyViewed = false;
    }

    public void setCurrentlyViewed(Boolean currentlyViewed) {
        this.currentlyViewed = currentlyViewed;
    }

    public String getChatHistory() {
        return chatHistory;
    }

    @Override
    public void run() {
        try {
            // Read and send messages
            String message;

            // Tells the client that the server is ready
            socketComm.sendDataToClient("Server is ready");

            while (true) {
                // Read message from client
                message = socketComm.getDataFromClient();

                if (message == null || message.equalsIgnoreCase(SocketComm.BYE)) {
                    textArea.appendText("Client disconnected.\n");
                    close();
                    break;
                }

                if (currentlyViewed) {
                    textArea.appendText("Client: " + message + "\n");
                }

                chatHistory += "Client: " + message + "\n";
            }

            // Close resources
            socketComm.close();
        } catch (IOException e) {
            textArea.appendText(e.getMessage());
        }
    }

    public void sendMessage(String message) {
        socketComm.sendDataToClient(message);
        textArea.appendText("You: " + message + "\n");
        chatHistory += "You: " + message + "\n";
    }

    public void close() throws IOException {
        socketComm.sendDataToClient(SocketComm.BYE);
        socketComm.close();
    }
}