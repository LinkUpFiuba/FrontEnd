package linkup.linkup.Utils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import linkup.linkup.model.Interests;
import linkup.linkup.model.Photo;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

/**
 * Created by andres on 9/4/17.
 */

public class DataBase {

    public static  void updateUser(User user){
        Map<String, Object> map = user.toMap();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(user.Uid).updateChildren(map);
    }
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
    public static void  saveUser(User user){
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();/**
        Gson gson = new Gson();

        //databaseReference.child("users").child(user.Uid).setValue(gson.toJson(user));
        databaseReference.child("users").child(user.Uid).child("name").setValue(user.name);

        databaseReference.child("users").child(user.Uid).child("photoUrl").setValue(user.photoUrl);
        databaseReference.child("users").child(user.Uid).child("email").setValue(user.email);
        databaseReference.child("users").child(user.Uid).child("birthday").setValue(user.birthday);
        databaseReference.child("users").child(user.Uid).child("age").setValue(user.age);
        databaseReference.child("users").child(user.Uid).child("gender").setValue(user.gender);
        databaseReference.child("users").child(user.Uid).child("range").setValue(user.range);
        databaseReference.child("users").child(user.Uid).child("likesList").setValue(user.likesList);
        databaseReference.child("users").child(user.Uid).child("education").setValue(user.education);
        databaseReference.child("users").child(user.Uid).child("work").setValue(user.work);
        databaseReference.child("users").child(user.Uid).child("invisibleMode").setValue(user.invisibleMode);

        databaseReference.child("users").child(user.Uid).child("linkUpPlus").setValue(user.linkUpPlus);

        databaseReference.child("users").child(user.Uid).child("interests").setValue(user.interests);

        databaseReference.child("users").child(user.Uid).child("photoList").setValue(user.photoList);
**/
        databaseReference.child("users").child(user.Uid).setValue(user);
    }
    public static void createOrGetUser(final FirebaseUser firebaseUser){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if(dataSnapshot.exists()){
                    user =(User)dataSnapshot.getValue(User.class);
                } else {
                    user=new User(firebaseUser);
                }
                SingletonUser.set(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
