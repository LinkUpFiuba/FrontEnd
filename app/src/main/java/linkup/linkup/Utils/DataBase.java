package linkup.linkup.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import linkup.linkup.model.Link;
import linkup.linkup.model.Unlink;
import linkup.linkup.model.User;


public class DataBase {


    private static final String TAG = "Database";

    public static User getUser(String Uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final User[] user = new User[1];
        ref.child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user[0] = (User) dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user[0];
    }

    public static void saveLink(final Link link) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("links").child(link.linkingUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(link.linkedUid, true);
                    dataSnapshot.getRef().updateChildren(update);
                } else {
                    dataSnapshot.getRef().child(link.linkedUid).setValue(true);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference possibleMatchesReference= databaseReference.child("possibleMatches");
        DatabaseReference  linkReference=possibleMatchesReference.push();
        linkReference.setValue(link).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Link completo");
                }else{
                }
            }
        });

    }

    public static void saveUnlink(final Unlink unlink) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("unlinks").child(unlink.unlinkingUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if (dataSnapshot.exists()) {

                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(unlink.unlinkedUid, true);
                    dataSnapshot.getRef().updateChildren(update);
                } else {
                    dataSnapshot.getRef().child(unlink.unlinkedUid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG,"Unlink completo");
                            }else{
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updateUser(final User user) {

        Map<String, Object> map = user.toMap();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();


        ref.child("users").child(user.Uid).updateChildren(map);
    }
}