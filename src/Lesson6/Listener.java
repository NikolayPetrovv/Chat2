package Lesson6;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {

    private Socket socket;
    private DataInputStream in;
    private int port;
    private boolean clientConnected;

    public Listener(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isClientConnected() {
        return clientConnected;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started");
            socket = serverSocket.accept();
            System.out.println("Client connected");
            clientConnected = true;
            DataInputStream in = new DataInputStream(socket.getInputStream());
            while (true) {
                String str = in.readUTF();
                if (str.equals("/stopListener")) {
                    break;
                }
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                in.close();
            } catch (IOException e) {

            }
        }
    }
}
