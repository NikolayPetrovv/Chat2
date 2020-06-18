package Lesson7_1;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void setAuthorized(boolean authorized) {
        Client.authorized = authorized;
    }

    static boolean authorized;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8189);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("/auth login1 pass1");
            setAuthorized(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            if(in.available()>0) {
                                String strFromServer = in.readUTF();
                                if (strFromServer.startsWith("/authOk")) {
                                    setAuthorized(true);
                                    System.out.println("Authorized on server");
                                    break;
                                }
                                System.out.println(strFromServer + "\n");
                            }
                        }
                        while (true) {
                            if (in.available()>0) {
                                String strFromServer = in.readUTF();
                                if (strFromServer.equalsIgnoreCase("/end")) {
                                    break;
                                }
                                System.out.println(strFromServer);
                                System.out.println("\n");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String message = reader.readLine();
                out.writeUTF(message);
                if (message.equals("/end")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
