package linkup.linkup.model;

import linkup.linkup.Utils.App;

/**
 * Created by andres on 9/6/17.
 */

public class SingletonUser {
    private static User instance;
    public static User get() {
        if(instance == null) instance = getSync();
        return instance;
    }
    public static void set(User user) {
        instance=user;
    }

    private static synchronized User getSync() {
        if(instance == null) instance = null;
        return instance;
    }

    private SingletonUser(){
        // here you can directly access the Application context calling
        App.get();
    }
}

