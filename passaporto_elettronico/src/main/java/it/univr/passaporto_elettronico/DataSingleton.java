package it.univr.passaporto_elettronico;

public class DataSingleton {

    private static final DataSingleton instance = new DataSingleton();

    private UserC user;

    public static DataSingleton getInstance(){
        return instance;
    }

    public UserC getUser(){
        return user;
    }

    public void setUser(UserC other){
        this.user = other;
    }
}
