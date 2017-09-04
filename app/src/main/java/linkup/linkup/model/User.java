package linkup.linkup.model;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import linkup.linkup.Utils.DataBase;


/**
 * Created by andres on 9/3/17.
 */
@IgnoreExtraProperties
public class User {
    public String Uid;
    Uri photoUrl;
    String name;
    String email;
    String birthday;
    String gender;
    int minRange;
    int maxRange;
    List<String> jobs;
    List<String> education;
    User user;
     JSONObject userJson;
    public User(){
    }
    public User(FirebaseUser firebaseUser){
        Uid =firebaseUser.getUid();
        photoUrl = firebaseUser.getPhotoUrl();
        name = firebaseUser.getDisplayName();
        email =firebaseUser.getEmail();
        List<UserInfo> userInfos = (List<UserInfo>) firebaseUser.getProviderData();
        user=this;
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        userJson=object;
                        try {
                            gender=object.getString("gender");
                            //birthday=object.getString("birthday");


                        }catch (JSONException e){
                            Log.d("User",e.toString());
                        }
                        DataBase.saveUser(user);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "gender,birthday,education,likes,work,photos");
        request.setParameters(parameters);
        request.executeAsync();


    }
}
