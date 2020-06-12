package source.server;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationServer {
    private class User {
        private String login;
        private String password;
        private String nickname;

        public User(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<User> userList;

    public AuthorizationServer() {
        userList = new ArrayList<User>();
        userList.add(new User("login1", "pass1", "nick1"));
        userList.add(new User("login2", "pass2", "nick2"));
        userList.add(new User("login3", "pass3", "nick3"));
    }

    public String getNickByLoginAndPwd(String login, String password) {
        for(User user: userList) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nickname;
            }
        }
        return null;
    }

}
