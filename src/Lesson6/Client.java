package Lesson6;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String host;
    private int port;

    public static void main(String[] args) throws IOException, InterruptedException {
        new Client("localhost", 8189);
    }

    public Client(String host, int port) throws IOException, InterruptedException {
        this.host = host;
        this.port = port;
        createConnection();
        Sender sender = new Sender(out);
        Recipient recipient = new Recipient(in, sender);
        sender.start();
        recipient.start();
        sender.join();
        closeConnection();
    }

    private void createConnection() throws IOException {
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    private void closeConnection() throws IOException {
        socket.close();
        in.close();
        out.close();
    }
}
