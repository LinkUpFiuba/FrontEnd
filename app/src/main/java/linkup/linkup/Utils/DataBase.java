package linkup.linkup.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import linkup.linkup.ProfileActivity;
import linkup.linkup.ViewProfileActivity;
import linkup.linkup.model.Block;
import linkup.linkup.model.Link;
import linkup.linkup.model.Report;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;
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

    public static void showProfileUser(final String Uid, final Context context){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        String myUserId = SingletonUser.getUser().getSerializableUser().getId();

        ref.child("blocks").child(myUserId).child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.getChildrenCount();
                if (count == 0){
                    checkDisebledUser(Uid,context);
                }else{
                    showAlertDialog(context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void checkDisebledUser(final String Uid, final Context context){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("disabledUsers").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() == false){
                    checkLike(Uid,context);
                }else{
                    showAlertDialog(context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void checkLink(final String Uid, final Context context){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("matches").child(SingletonUser.getUser().Uid).child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() == false){
                    getProfileUser(Uid,context,true);
                }else{
                    getProfileUser(Uid,context,false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static void checkLike(final String Uid, final Context context){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("links").child(SingletonUser.getUser().Uid).child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() == false){
                    checkLink(Uid,context);
                }else{
                    getProfileUser(Uid,context,false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private static void getProfileUser(String Uid, final Context context, final boolean showGameButtons){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = (User) dataSnapshot.getValue(User.class);
                if (user.invisibleMode == true){
                    showAlertDialog(context);

                }else {
                    Intent i;
                    if(showGameButtons == true){
                        i = new Intent(context, ProfileActivity.class);
                        i.putExtra("callDatabse", true);
                    }else{
                        i = new Intent(context, ViewProfileActivity.class);
                    }
                    i.putExtra("user", user.getSerializableUser());
                    context.startActivity(i);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private static void showAlertDialog(Context context) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context).setTitle("Atención").setMessage("No puedes visualizar el perfil de esta persona");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }

        }).show();
    }


    public static void postLike(final SerializableUser otherUser) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("links").child(SingletonUser.getUser().Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(otherUser.getId(), Link.NORMAL);
                    dataSnapshot.getRef().updateChildren(update);
                }else {
                    dataSnapshot.getRef().child(otherUser.getId()).setValue(Link.NORMAL);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static void postSuperLike(final SerializableUser otherUser) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("links").child(SingletonUser.getUser().Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(otherUser.getId(), Link.SUPERLINK);
                    dataSnapshot.getRef().updateChildren(update);
                }else {
                    dataSnapshot.getRef().child(otherUser.getId()).setValue(Link.NORMAL);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void postDontLike(final SerializableUser otherUser) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("unlinks").child(SingletonUser.getUser().Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put(otherUser.getId(), true);
                    dataSnapshot.getRef().updateChildren(update);
                }else {
                    dataSnapshot.getRef().child(otherUser.getId()).setValue(Link.NORMAL);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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