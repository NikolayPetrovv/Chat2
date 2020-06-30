package Lesson8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) throws SQLException {
        this.myServer = myServer;
        this.socket = socket;
        this.name = "";
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(()-> {
                try {
                    authenticate();
                    readMessages();
                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException ex) {
            throw new RuntimeException("Client creation error");
        }
    }

    private void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        myServer.unsubscribe(this);
        myServer.broadcast("User " + name + "left");
    }

    private void readMessages() throws IOException, SQLException {
        while (true) {
            if (in.available()>0) {
                String message = in.readUTF();
                System.out.println("From " + name + ":" + message);
                if (message.equals("/end")) {
                    return;
                }

                if (message.startsWith("/rename")) {
                    String[] parts = message.split("\\s");
                    Statement statement = myServer.getAuthService().getConn().createStatement();
                    statement.executeUpdate("UPDATE users SET nick = '" + parts[1] + "'" + " where nick = '" + this.name + "'");
                    sendMsg(this.name + " renamed to " + parts[1]);
                    this.name = parts[1];
                    statement.close();
                } else if (message.startsWith("/w ")) {
                    String[] parts = message.split("\\s");
                    myServer.sendDirect(parts[1],name+ ": "+ parts[2]);
                } else {
                    myServer.broadcast(name + ": " + message);
                }
            }
        }
    }

//    private String validateMessage(String message) {
//        String result;
//        result = message.replace("россия", "Россия");
//        result = result.replace("дурак", "ду##к");
//        return result;
//    }

    private void printUserListDB() throws SQLException {
        Statement statement =  myServer.getAuthService().getConn().createStatement();
        ResultSet rs = statement.executeQuery("select * from users");

        while (rs.next()){
            System.out.println(rs.getString("nick"));
        }
        rs.close();

    }

    private void authenticate() throws IOException {
        while(true) {
            if (in.available()>0){
                String str = in.readUTF();
                if (str.startsWith("/auth")) {
                    String[] parts = str.split("\\s");
                    String nick = myServer.getAuthService().getNickByLoginAndPwd(parts[1], parts[2]);
                    if (nick != null) {
                        if (!myServer.isNickLogged(nick)) {
                            System.out.println(nick + " logged into chat");
                            name = nick;
                            sendMsg("/authOk " + nick);
                            myServer.broadcast(nick + " is in chat");
                            myServer.subscribe(this);
                            return;
                        } else {
                            System.out.println("User " + nick + " tried to re-enter");
                            sendMsg("User already logged in");
                        }
                    } else {
                        System.out.println("Wrong login/password");
                        sendMsg("Incorrect login attempted");
                    }
                }
            }

        }
    }

    public void sendMsg(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}