package main.java;

import java.io.Serializable;

public class Campaign implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private char[] password;

    public Campaign(String name, char[] password) {
        this.name = name;
        this.password = password;
    }
    public char[] getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return name;
    }
}
