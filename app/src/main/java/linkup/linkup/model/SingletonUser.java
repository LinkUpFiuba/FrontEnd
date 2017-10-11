package linkup.linkup.model;

import linkup.linkup.Utils.App;

/**
 * Created by andres on 9/6/17.
 */

public class SingletonUser {
    private static User instance;
    private static String token;

    public static User getUser() {
        if(instance == null) instance = getSync();
        return instance;
    }
    public static void setUser(User user) {
        instance=user;
    }
    public static void setToken(String userToken) {
        token=userToken;
    }
    public static String getToken(){
        if(token == null) token = getSyncToken();
        return token;
    }

    private static synchronized User getSync() {
        if(instance == null) instance = new User();
        return instance;
    }
    private static synchronized String getSyncToken() {
        if(token == null) token = "";
        return token;
    }
    private SingletonUser(){
        App.get();
    }
}

