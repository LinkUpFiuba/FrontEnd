package linkup.linkup.Utils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

import linkup.linkup.model.Interests;
import linkup.linkup.model.Photo;
import linkup.linkup.model.User;

/**
 * Created by andres on 9/4/17.
 */

public class DataBase {

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
    public static void  userExists(final FirebaseUser firebaseUser){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                } else {
                    User user=new User(firebaseUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
