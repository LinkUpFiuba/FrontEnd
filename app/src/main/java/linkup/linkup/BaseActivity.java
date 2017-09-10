package linkup.linkup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import linkup.linkup.Utils.DataBase;
import linkup.linkup.Utils.HttpClient;
import linkup.linkup.model.Education;
import linkup.linkup.model.Like;
import linkup.linkup.model.Photo;
import linkup.linkup.model.Range;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;
import linkup.linkup.model.Work;

import static android.R.attr.data;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
    public void createOrGetUser(final FirebaseUser firebaseUser){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String token = FirebaseInstanceId.getInstance().getToken();
        ref.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if(dataSnapshot.exists()){
                    user =(User)dataSnapshot.getValue(User.class);
                    Log.d(TAG, "Usuario existente");
                    SingletonUser.set(user);
                    startMainActivity();
                } else {
                    Log.d(TAG, "Usuario no existente");
                    user = new User(firebaseUser);
                    getFbInformationForUser(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void getFbInformationForUser(final User user){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            Log.d(TAG,object.toString());
                            if(object.has("gender")) {
                                user.gender = object.getString("gender");
                            }
                            if(object.has("birthday")){
                                String birthday=object.getString("birthday");
                                DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                                DateTime dt = formatter.parseDateTime(birthday);
                                DateTime today= DateTime.now();
                                Period period=new Period(dt,today);
                                int years= period.getYears();
                                String age = String.valueOf(years);
                                Range range = new Range(years);
                                user.birthday = birthday;
                                user.age = age;
                                user.range = range;
                            }
                            if(object.has("education")) {
                                List<Education> educationList = Education.educationList(object.getJSONArray("education"));
                                user.education = educationList.get(educationList.size()-1).name;
                            }
                            if(object.has("likes")) {
                                List<Like> likesList = Like.likesList(object.getJSONObject("likes").getJSONArray("data"));
                                user.likesList = likesList;
                            }
                            if(object.has("work")) {
                                List<Work> workList= Work.workList(object.getJSONArray("work"));
                               user.work = workList.get(workList.size()-1).name;
                            }
                            if(object.has("photos")) {
                                user.photoList = Photo.photoList(object.getJSONObject("photos").getJSONArray("data"));
                            }

                        }catch (JSONException e){
                            Log.d("User",e.toString());
                        }
                        saveUser(user);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "gender,birthday,education,likes,work,photos,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void  saveUser(final User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("users").child(user.Uid).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    Log.d(TAG, "Data saved successfully.");

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(BaseActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    builder.setTitle("Todo listo");
                    builder.setMessage("Bienvenido a LinkUp!");
                    builder.setIcon(R.drawable.ic_check_white_24dp);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(TAG, "Exito");
                            SingletonUser.set(user);
                            startMainActivity();
                        }

                    });
                    builder.show();
                }
            }
        });
    }

    public  void updateUser(final User user){
        Map<String, Object> map = user.toMap();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(user.Uid).updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    Log.d(TAG, "Data saved successfully.");

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(BaseActivity.this, R.style.AppTheme);
                    builder.setTitle(getResources().getString(R.string.edit_success_title));
                    builder.setMessage(getResources().getString(R.string.edit_success_message));
                    builder.setIcon(R.drawable.ic_check_white_24dp);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(TAG, "Exito");
                            onBackPressed();
                        }

                    });
                    builder.show();
                }
            }
        });
    }

    public void startMainActivity(){
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
    }
    public void logOut(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        startActivity(intent);
    }
}
