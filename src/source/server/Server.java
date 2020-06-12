package source.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import source.client.ClientHandler;
import source.server.AuthorizationServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 8030;
    private List<ClientHandler> clients;
    private AuthorizationServer authorizationServer;

    public AuthorizationServer getAuthService() {
        return authorizationServer;
    }



    private static List<String> clientsList = new ArrayList<>();

    public List<String> getClientsList() {
        return clientsList;
    }

    public void addClientToList(String nickname) {
        clientsList.add(nickname);
    }

    public void removeClientFromList(String nickname) {
        clientsList.remove(nickname);
    }




    public Server() {
        clientsList.add("Список клиентов:");
        try (ServerSocket server = new ServerSocket(PORT)) {
            authorizationServer = new AuthorizationServer();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Server awaits clients");
                Socket socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException ex) {
            System.out.println("Server error");
        }
    }


    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void broadcast(String s) {
        for (ClientHandler client : clients) {
            client.sendMessage(s);
        }
    }

    public synchronized void sendPrivateMessage(String fromUser, String nick, String message) {
        if (isNickLogged(nick)) {
            for (ClientHandler client : clients) {
                if (client.getNickname().equals(nick)) {
                    client.sendMessage(fromUser + " whispers to you: " + message);
                }
                if (client.getNickname().equals(fromUser)) {
                    client.sendMessage("You whispers to " + nick + ": " + message);
                }
            }
        } else {
            for (ClientHandler client : clients) {
                if (client.getNickname().equals(fromUser)) {
                    client.sendMessage("Server: " + nick + " is offline, try later.");
                    break;
                }
            }
        }
    }

    public synchronized boolean isNickLogged(String nick) {
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(nick)) {
                return true;
            }
        }
        return false;
    }

}
