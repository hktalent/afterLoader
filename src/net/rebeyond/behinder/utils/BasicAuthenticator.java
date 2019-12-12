package net.rebeyond.behinder.utils;

import java.net.PasswordAuthentication;

public class BasicAuthenticator extends java.net.Authenticator {
    String userName;
    String password;

    public BasicAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }


    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.userName, this.password.toCharArray());
    }
}
