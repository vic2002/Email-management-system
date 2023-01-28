package server.model;

import java.sql.Timestamp;

/** Log in object corresponding to the JSON body sent by client when logging in. */
public class LoginModel {
    private String login;
    private String password;
    private Timestamp timestamp;

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public LoginModel() { }

    public LoginModel(String login, String password, Timestamp timestamp) {
        this.login = login;
        this.password = password;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return this.login + this.password + this.timestamp;
    }
}