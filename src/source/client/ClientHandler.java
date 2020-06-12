package source.client;


import source.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String nickname;

    public String getNickname() {
        return nickname;
    }


    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.nickname = "";
        try {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authenticate();
                    readMessages();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания клиента.");
        }

    }


    private void closeConnection() {
        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        server.unsubscribe(this);
        server.broadcast("Пользователь " + nickname + " вышел из чата.");
        System.out.println(nickname + " отключился от сервера.");
    }

    private void readMessages() throws IOException {
        while (true) {
            if (dataInputStream.available() > 0) {
                String message = dataInputStream.readUTF();
                if (message.startsWith("/")) {
                    String[] tmp = message.split(" ", 3);
                    if (message.startsWith("/w")) {
                        server.sendPrivateMessage(nickname, tmp[1], tmp[2]);
                    } else if (message.equals("/end")) {
                        server.unsubscribe(this);
                        server.removeClientFromList(nickname);
                        return;
                    }
                } else {
                    server.broadcast(nickname + ": " + message);
                }
            }
        }
    }

    private void authenticate() throws IOException {
        while (true) {
            if (dataInputStream.available() > 0) {
                String str = dataInputStream.readUTF();
                if (str.startsWith("/auth")) {
                    String[] parts = str.split("\\s");
                    String nick = server.getAuthService().getNickByLoginAndPwd(parts[1], parts[2]);
                    if (nick != null) {
                        if (!server.isNickLogged(nick)) {
                            System.out.println(nick + " logged into chat");
                            nickname = nick;
                            sendMessage("/auth OK");
                            server.broadcast(nick + " is in chat");
                            server.subscribe(this);
                            server.addClientToList(nick);
                            return;
                        } else {
                            System.out.println("User " + nick + " tried to re-enter");
                            sendMessage("User already logged in");
                        }
                    } else {
                        System.out.println("Wrong login/password");
                        sendMessage("false");
                    }
                }
            }

        }
    }

    public void sendMessage(String s) {
        try {
            dataOutputStream.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
