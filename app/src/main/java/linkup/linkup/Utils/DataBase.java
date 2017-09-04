package linkup.linkup.Utils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import linkup.linkup.model.User;

/**
 * Created by andres on 9/4/17.
 */

public class DataBase {

    public static void  saveUser(User user){
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        Gson gson = new Gson();
        databaseReference.child("users").child(user.Uid).setValue(gson.toJson(user));
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
