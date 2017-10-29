package linkup.linkup.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import linkup.linkup.LinkFragment;
import linkup.linkup.model.Block;
import linkup.linkup.model.Link;
import linkup.linkup.model.Report;
import linkup.linkup.model.Unlink;
import linkup.linkup.model.User;

import static linkup.linkup.R.string.block;
import static linkup.linkup.R.string.report;


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
                    update.put(link.linkedUser, link.type);
                    dataSnapshot.getRef().updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            linkFragment.ifCardsDepletedStartAnimation();
                        }
                    });
                } else {
                    dataSnapshot.getRef().child(link.linkedUser).setValue(link.type).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        databaseReference.child("links").child(uIdBlocking).child(uIdBlocked).removeValue();
        databaseReference.child("links").child(uIdBlocked).child(uIdBlocking).removeValue();

        databaseReference.child("blocks").child(uIdBlocking).child(uIdBlocked).child("by").setValue(uIdBlocking);
        databaseReference.child("blocks").child(uIdBlocked).child(uIdBlocking).child("by").setValue(uIdBlocking);

        databaseReference.child("matches").child(uIdBlocking).child(uIdBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Block block = new Block(false, uIdBlocking);
                    databaseReference.child("matches").child(uIdBlocked).child(uIdBlocking).child("block").setValue(block);
                    databaseReference.child("matches").child(uIdBlocking).child(uIdBlocked).removeValue();
                    databaseReference.child("messages").child(uIdBlocking).child(uIdBlocked).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void saveDeleteLink(final String uIdBlocking,final String uIdBlocked) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("links").child(uIdBlocking).child(uIdBlocked).removeValue();
        databaseReference.child("links").child(uIdBlocked).child(uIdBlocking).removeValue();

        databaseReference.child("matches").child(uIdBlocking).child(uIdBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Block block = new Block(false, uIdBlocking);
                    block.setType(Block.DELETE_LINK);

                    databaseReference.child("matches").child(uIdBlocked).child(uIdBlocking).child("block").setValue(block);

                    databaseReference.child("matches").child(uIdBlocking).child(uIdBlocked).removeValue();
                    databaseReference.child("messages").child(uIdBlocking).child(uIdBlocked).removeValue();
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

    public static void deleteBloquedMatch(final String uIdBlocking,final String uIdBlocked) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("matches").child(uIdBlocked).child(uIdBlocking).removeValue();
        databaseReference.child("messages").child(uIdBlocked).child(uIdBlocking).removeValue(); }
}