package linkup.linkup.Utils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import linkup.linkup.model.User;

/**
 * Created by andres on 9/4/17.
 */

public class DataBase {

    public static void  saveUser(User user){
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("users").child(user.Uid).setValue(user);
    }
}
