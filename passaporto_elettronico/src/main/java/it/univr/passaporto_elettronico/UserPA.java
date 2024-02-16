package it.univr.passaporto_elettronico;

import java.io.Serial;
import java.io.Serializable;

public class UserPA implements Serializable, Comparable<UserPA> {
    @Serial
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    public UserPA() {
        this.username = "";
        this.password = "";
    }

    public UserPA(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof UserPA other &&
                this.username.equalsIgnoreCase(other.getUsername()) &&
                this.password.equals(other.getPassword());
    }

    @Override
    public int compareTo(UserPA other) {
       return other != null ?
               this.username.compareTo(other.username) :
               0;
    }
}
