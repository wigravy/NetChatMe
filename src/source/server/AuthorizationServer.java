package source.server;

import java.sql.*;


public class AuthorizationServer {
    private static Connection connectionToDb;
    private static final String USER_FOR_DB = "server";
    private static final String PASSWORD_FOR_DB = "Qbnl=U76$3nb%45Il";
    private static final String URL_FOR_DB = "jdbc:postgresql://127.0.0.1:5432/netchatdb";


    private void connectToDB() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found.");
            e.printStackTrace();
            return;
        }
        System.out.println("PostgreSQL JDBC Driver successfully connected");
        try {
            connectionToDb = DriverManager
                    .getConnection(URL_FOR_DB, USER_FOR_DB, PASSWORD_FOR_DB);
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
            return;
        }
        System.out.println("Connected to PostgreSQL");
    }

    AuthorizationServer() {
        connectToDB();
    }

    public void closeConnection() {
        try {
            if (!connectionToDb.isClosed()) {
                connectionToDb.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean changeNickname(String login, String nickname) {
        try {
            PreparedStatement statement = connectionToDb.prepareStatement("UPDATE users SET nickname = '" + nickname + "' WHERE login = '" + login + "';");
            int result = statement.executeUpdate();
            return result > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    public String getNickByLoginAndPwd(String login, String password) {
        try {
            PreparedStatement statement = connectionToDb.prepareStatement("SELECT nickname FROM users WHERE login = '" + login + "' AND password = '" + password + "';");
            ResultSet result = statement.executeQuery();
            result.next();
            String nickname = result.getString(1);
            if (result != null) {
                return nickname;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

}
