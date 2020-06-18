package Lesson6;

import java.io.*;
import java.net.Socket;

public class Server {

    private Socket socket;
    private DataOutputStream out;

    public static void main(String[] args) throws IOException {
        new Server(8189);
    }

    public Server(int port) {

        Listener listener = new Listener(port);
        listener.start();

        while (true) {
            try {
                Thread.sleep(1000);
                if (listener.isClientConnected()) {
                    out = new DataOutputStream(listener.getSocket().getOutputStream());
                    break;
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        Sender sender = new Sender(out);
        sender.start();
    }
}
