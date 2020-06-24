package source.client;


import source.server.Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private String nickname;
    private String login;


    public String getNickname() {
        return nickname;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
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
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Error creating client");
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
        server.broadcast("User  " + nickname + " leave chat.");
        System.out.println(nickname + " disconnected from server.");
    }

    private void readMessages() throws IOException {
        while (true) {
            if (dataInputStream.available() > 0) {
                String message = dataInputStream.readUTF();
                if (message.startsWith("/")) {
                    String[] tmp = message.split("\\s", 3);
                    if (message.startsWith("/w")) {
                        if (nickname.equals(tmp[1])) {
                            sendMessage("Server: You cannot send private messages to yourself");
                        } else {
                            server.sendPrivateMessage(nickname, tmp[1], tmp[2]);
                        }
                    } else if (message.equals("/end")) {
                        closeConnection();
                        return;
                    } else if (message.startsWith("/changenick")){
                        String previousNick = nickname;
                        if (server.getAuthService().changeNickname(login, tmp[1])) {
                            changeNickname(tmp[1]);
                            server.updateNick(previousNick, tmp[1]);
                            server.broadcast(previousNick + " change nickname to " + tmp[1]);
                        } else {
                            sendMessage("Server: failed to change nickname");
                        }
                    }
                } else {
                    server.broadcast(getCurrentTime() + " " + nickname + ": " + message);
                }
            }
        }
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    private void authenticate() throws IOException {
        long timeout = System.currentTimeMillis() + 120000;
        while (true) {
            if (System.currentTimeMillis() < timeout) {
                if (dataInputStream.available() > 0) {
                    String str = dataInputStream.readUTF();
                    if (str.startsWith("/auth")) {
                        String[] parts = str.split("\\s");
                        String nick = server.getAuthService().getNickByLoginAndPwd(parts[1], parts[2]);
                        if (nick != null) {
                            if (!server.isNickLogged(nick)) {
                                System.out.println(nick + " logged into chat");
                                nickname = nick;
                                login = parts[1];
                                sendMessage("/auth OK");
                                server.broadcast(nick + " is in chat");
                                server.subscribe(this);
                                return;
                            } else {
                                System.out.println("User " + nick + " tried to re-enter");
                                sendMessage("User already logged in");
                            }
                        } else {
                            System.out.println("Wrong login/password");
                            sendMessage("/false");
                        }
                    }
                }
            } else {
                sendMessage("/end");
                System.out.println("The client was disconnected for inaction (More than 120 seconds passed)");
                socket.close();
                break;
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
