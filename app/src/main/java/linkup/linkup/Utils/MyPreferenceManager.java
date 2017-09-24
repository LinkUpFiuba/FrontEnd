package linkup.linkup.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by german on 9/24/2017.
 */

public class MyPreferenceManager {
    private String TAG = MyPreferenceManager.class.getSimpleName();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    Context _context;
    private static final String PREF_NAME = "linkup";

    private static final String KEY_NOTIFICATIONS = "notifications";

    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void addNotification(String notification) {

        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }
    public void clear() {
        editor.clear();
        editor.commit();
    }
}
