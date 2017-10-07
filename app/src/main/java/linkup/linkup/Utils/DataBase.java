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

import linkup.linkup.LinkFragment;
import linkup.linkup.model.Link;
import linkup.linkup.model.Report;
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

    public static void saveLink(final Link link, final LinkFragment linkFragment) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("links").child(link.linkingUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(link.linkedUser, true);
                    dataSnapshot.getRef().updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            linkFragment.ifCardsDepletedStartAnimation();
                        }
                    });
                } else {
                    dataSnapshot.getRef().child(link.linkedUser).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            linkFragment.ifCardsDepletedStartAnimation();
                        }
                    });

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
    public static void saveBlock(final String uIdBlocking,final String uIdBlocked) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("blocks").child(uIdBlocking).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(uIdBlocked, true);
                    dataSnapshot.getRef().updateChildren(update);
                } else {
                    dataSnapshot.getRef().child(uIdBlocked).setValue(true);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("messages").child(uIdBlocking).child(uIdBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    dataSnapshot.getRef().removeValue();
                    databaseReference.child("messages").child(uIdBlocked).child(uIdBlocking).child("blocked").setValue(true);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public static void saveReport(Report report) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference  reportReference= databaseReference.child("complaints").child(report.idReported).push();
        reportReference.setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public static void saveUnlink(final Unlink unlink, final LinkFragment linkFragment) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("unlinks").child(unlink.unlinkingUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if (dataSnapshot.exists()) {

                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(unlink.unlinkedUser, true);
                    dataSnapshot.getRef().updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            linkFragment.ifCardsDepletedStartAnimation();
                        }
                    });
                } else {
                    dataSnapshot.getRef().child(unlink.unlinkedUser).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                                linkFragment.ifCardsDepletedStartAnimation();
                            
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