package linkup.linkup.Utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import linkup.linkup.model.Interests;
import linkup.linkup.model.Photo;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;


public class DataBase {


    private static final String TAG = "Database";

    public static User getUser(String Uid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final User[] user = new User[1];
        ref.child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user[0] =(User)dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user[0];
    }
    public static void saveLink(final String Uid) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final User myUser = SingletonUser.getUser();
        databaseReference.child("links").child(myUser.Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if (dataSnapshot.exists()) {

                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(Uid, true);
                    dataSnapshot.getRef().updateChildren(update);
                } else {
                    dataSnapshot.getRef().child(Uid).setValue(true);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static void saveUnlink(final String Uid){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final User myUser = SingletonUser.getUser();
        databaseReference.child("unlinks").child(myUser.Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if (dataSnapshot.exists()) {

                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(Uid, true);
                    dataSnapshot.getRef().updateChildren(update);
                } else {
                    dataSnapshot.getRef().child(Uid).setValue(true);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
