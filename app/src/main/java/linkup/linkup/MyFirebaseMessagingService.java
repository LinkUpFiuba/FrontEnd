package linkup.linkup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import linkup.linkup.Utils.Config;
import linkup.linkup.Utils.NotificationUtils;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.User;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
        processNewNotification(remoteMessage);
    }

    private void processNewNotification(RemoteMessage message) {
        RemoteMessage.Notification notification = message.getNotification();
        String notificationBody = "";
        String notificationTitle = "";
        String notifitacionTimestamp = "";

        if (notification != null) {
            notificationBody = message.getNotification().getBody();
            notificationTitle = message.getNotification().getTitle();
            notifitacionTimestamp = Long.toString(message.getSentTime());
        }

        Map<String, String> data = message.getData();
        String type = data.get("type");
        if(type!=null) {
            if (type.equals("disable")) {
                processNewDisableNotification(data, notificationBody, notificationTitle, notifitacionTimestamp);
            }
            if (type.equals("match")) {
                processNewMatchNotification(data, notificationBody, notificationTitle, notifitacionTimestamp);
            }
            if (type.equals("message")) {
                //TODO
            }
        }
    }
    private void processNewMatchNotification(Map<String, String> data,String notificationBody,String notificationTitle,String notifitacionTimestamp){
        String userId = data.get("Uid");
        String photoUrl = data.get("photo");
        String name = data.get("name");
        Log.d(TAG, "name " + name + " photo " + photoUrl);

        notificationBody =  name + " " +notificationBody;

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Log.d(TAG, "New match received from user_id: " + userId);
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            showNewMatchActivity(data);
        } else {

            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("type", Config.PUSH_TYPE_NEW_MATCH);
            resultIntent.putExtra("userId", userId);

            showNotificationMessageWithBigImage(getApplicationContext(), notificationTitle, notificationBody, notifitacionTimestamp, resultIntent,photoUrl);
        }
    }
    private void processNewDisableNotification(Map<String, String> data,String notificationBody,String notificationTitle,String notifitacionTimestamp){
        Intent resultIntent = new Intent(getApplicationContext(), BlockedActivity.class);
        showNotificationMessage(getApplicationContext(), notificationTitle, notificationBody, notifitacionTimestamp, resultIntent);
        startActivity(resultIntent);

    }
    private void showNewMatchActivity(Map<String, String> data) {
        String userId = data.get("Uid");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if (dataSnapshot.exists()) {
                    user = (User) dataSnapshot.getValue(User.class);
                    SerializableUser userSerial = user.getSerializableUser();
                    Log.d(TAG, userSerial.getName() + userSerial.getAge() + userSerial.getPhotoURL());

                    Intent intent = new Intent(getApplicationContext(), NewMatchActivity.class);
                    intent.putExtra("user", user.getSerializableUser());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}