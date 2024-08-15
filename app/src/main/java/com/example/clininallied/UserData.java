package com.example.clininallied;

import java.util.Map;

public class UserData {
     static String email;
     static String passsword;
    static String Username;

    public UserData() {
    }
    public UserData(String email , String passsword , String username){
        this.email = email;
        this.passsword = passsword;
        this.Username = username;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserData.email = email;
    }

    public static String getPasssword() {
        return passsword;
    }

    public static void setPasssword(String passsword) {
        UserData.passsword = passsword;
    }

    public static String getUsername() {
        return Username;
    }

    public static void setUsername(String username) {
        Username = username;
    }
    
}
