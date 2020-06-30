package Lesson8;

import org.h2.jdbc.JdbcSQLException;
import org.h2.message.DbException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAuthService {

    private Connection conn = null;

    private class User {
        private String login;
        private String passwd;
        private String nick;

        public User(String login, String passwd, String nick) {
            this.login = login;
            this.passwd = passwd;
            this.nick = nick;
        }
    }

    private List<User> userList;

    public DatabaseAuthService() {
        userList = new ArrayList<>();

        try {
            conn = DriverManager.
                    getConnection("jdbc:h2:~/test", "sa", "");
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            try {
                rs = statement.executeQuery("select * from users");
            }catch (JdbcSQLException ex) {
                statement.execute("create table users(" +
                        "login varchar(100), " +
                        "password varchar(100)," +
                        "nick varchar2(100));" +
                        "insert into users(login, password, nick) values('login1', 'password1', 'nick1');" +
                        "insert into users(login, password, nick) values('login2', 'password2', 'nick2');" +
                        "insert into users(login, password, nick) values('login3', 'password3', 'nick3');");
                rs = statement.executeQuery("select * from users");
            }
            while (rs.next()) {
                String login = rs.getString("login");
                String passwd = rs.getString("password");
                String nick = rs.getString("nick");
                userList.add(new User(login, passwd, nick));
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    public void start() {
        System.out.println("Authentication service started");
    }

    public void stop() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Authentication service stopped");
    }

    public String getNickByLoginAndPwd(String login, String passwd) {
        for(User user: userList) {
            if (user.login.equals(login) && user.passwd.equals(passwd)) {
                return user.nick;
            }
        }
        return null;
    }

    public Connection getConn() {
        return conn;
    }
}
