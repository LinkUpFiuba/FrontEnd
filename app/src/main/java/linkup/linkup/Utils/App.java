package linkup.linkup.Utils;

import android.app.Application;

/**
 * Created by andres on 9/6/17.
 */

public class App extends Application {
    private static App instance;
    private MyPreferenceManager pref;

    public static App get() { return instance; }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }
        return pref;
    }
}