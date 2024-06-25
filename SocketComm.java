package assignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketComm implements AutoCloseable {
    public static final int PORT = 1234;
    public static final String BYE = "byebye";

    private Socket socketClient;
    private BufferedReader reader;
    private PrintWriter writer;

    public SocketComm(Socket socketClient) throws IOException {
        this.socketClient = socketClient;
        this.createCommunicationLine();
    }

    private void createCommunicationLine() throws IOException {
        reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        writer = new PrintWriter(socketClient.getOutputStream(), true);
    }

    public String getDataFromClient() throws IOException {
        return reader.readLine(); // will wait for the other side to send something
    }

    public void sendDataToClient(String data) {
        writer.println(data);
    }

    public void close() {
        System.out.println("GoodBye");

        writer.close();
        try {
            reader.close();
            socketClient.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
