package Lesson8;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyServer {
    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private DatabaseAuthService authService;
    private File fileHistory = null;
    private BufferedWriter bufferedWriter = null;
    private BufferedReader bufferedReader = null;
    private Map<String, String> wordsForReplace = null;

    public DatabaseAuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new DatabaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Server awaits clients");
                Socket socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException | SQLException ex) {
            System.out.println("Server error");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }


    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
        showHistory(clientHandler.getName());
    }

    public synchronized void broadcast(String s) {

        for (ClientHandler client : clients) {
            client.sendMsg(validateMessage(s));
        }
        writeHistory(s);
    }

    private String validateMessage(String message) {

        if (wordsForReplace == null) {
            wordsForReplace = new HashMap<>();
            File fileWordsForReplace = new File("src/wordsForReplace.txt");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileWordsForReplace));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    wordsForReplace.put(parts[0], parts[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String result = message;

        for(Map.Entry<String, String> pair : wordsForReplace.entrySet()) {
            if (result.contains(pair.getKey())) {
                result = result.replace(pair.getKey(), pair.getValue());
            }
        }
        return result;
    }

    private void initFileHistory() {
        Path path = Paths.get("history.txt");
        fileHistory = new File(path.toString());

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileHistory, true));
            bufferedReader = new BufferedReader(new FileReader(fileHistory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHistory(String message) {

        if (message.startsWith("/")) return;

        if (bufferedWriter == null) {
            initFileHistory();
        }

        try {
            bufferedWriter.write(message + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showHistory(String nick) {

        ClientHandler cl = null;
        for (ClientHandler client : clients) {
            if (client.getName().equals(nick)) {
                cl = client;
            }
        }

        String line = null;
        try {
            line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                cl.sendMsg(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler o : clients) {
            sb.append(o.getName() + " ");
        }
        broadcast(sb.toString());
    }

    public synchronized boolean isNickLogged(String nick) {
        for (ClientHandler client : clients) {
            if (client.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void sendDirect(String nick, String message) {
        for (ClientHandler client : clients) {
            if (client.getName().equals(nick)) {
                client.sendMsg(validateMessage(message));
                return;
            }
        }
        System.out.println("Unknown nick - message not sent");
    }
}
