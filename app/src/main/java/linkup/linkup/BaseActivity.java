package linkup.linkup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import linkup.linkup.Utils.App;
import linkup.linkup.Utils.DataBase;
import linkup.linkup.model.Education;
import linkup.linkup.model.Like;
import linkup.linkup.model.Photo;
import linkup.linkup.model.Range;
import linkup.linkup.model.Report;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;
import linkup.linkup.model.Work;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public interface Command{
        public void excute();
    }

    public void setTimeOutConnectionAction(final Command command){

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(BaseActivity.this, R.style.AppThemeDialog);
        builder.setMessage(getResources().getString(R.string.create_user_error_fireabase_get));
        builder.setIcon(R.drawable.ic_clear_white_24dp);
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                command.excute();
            }

        });

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(mProgressDialog!=null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    if(!isFinishing()) {

                        builder.show();}
                }
            }
        }, 10000);
    }
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(R.style.AppThemeDialog);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);

        }
        if(!isFinishing()) {

            mProgressDialog.show();
        }
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
    // [START auth_with_facebook]
    public void getIDToken(){
        Log.d(TAG, "signInWithCredential:success");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if(task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    SingletonUser.setToken(idToken);
                    showProgressDialog();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    createOrGetUser(firebaseUser);

                    Log.d(TAG, "GetTokenResult result = " + idToken);
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(BaseActivity.this, "Fallo la autentificacion.",
                            Toast.LENGTH_LONG).show();
                    LoginManager.getInstance().logOut();
                    hideProgressDialog();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }

            }


        });
    }


    private void createOrGetUser(final FirebaseUser firebaseUser){
        showProgressDialog();


        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        setTimeOutConnectionAction(new Command() {
            @Override
            public void excute() {
                ref.goOffline();
                startLoginActivity();
            }
        });
        ref.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if(dataSnapshot.exists()){
                    user =(User)dataSnapshot.getValue(User.class);
                    Log.d(TAG, "Usuario existente");
                    user.tokenFCM= FirebaseInstanceId.getInstance().getToken();

                    hideProgressDialog();
                    if(!(user.gender.equalsIgnoreCase("male") || user.gender.equalsIgnoreCase("female"))){
                        SingletonUser.setUser(user);
                        startMissingInformationActivityForResult();
                    }else {
                        SingletonUser.setUser(user);
                        hideProgressDialog();

                        startMainActivity();
                    }
                } else {
                    Log.d(TAG, "Usuario no existente");
                    user = new User(firebaseUser);
                    user.tokenFCM= FirebaseInstanceId.getInstance().getToken();
                    getFbInformationForUser(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(BaseActivity.this, R.style.AppThemeDialog);
                builder.setMessage(getResources().getString(R.string.create_user_error_fireabase_get));
                builder.setIcon(R.drawable.ic_clear_white_24dp);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        startLoginActivity();
                    }

                });
                if(!isFinishing()) {

                    builder.show();
                }
            }
        });
    }

    public void startLoginActivity(){
    Intent i = new Intent(this, LoginActivity.class);
    startActivity(i);
    finish();
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
                            SingletonUser.setUser(user);

                            JSONObject likes=object.getJSONObject("likes");
                            getLikes(likes);
                        }catch (JSONException e){
                            Log.d("User",e.toString());
                        }

                        if(!(user.gender.equalsIgnoreCase("male") || user.gender.equalsIgnoreCase("female"))){
                            hideProgressDialog();
                            startMissingInformationActivityForResult();
                        }else {

                            saveUser(user);
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "gender,birthday,education,likes,work,photos,id");
        request.setParameters(parameters);
        request.executeAsync();
    }
    public void getLikes(JSONObject likes) throws JSONException {
        JSONObject paging=likes.getJSONObject("paging");
        if(paging.has("next")) {
            String stringNext = paging.getString("next");
            String[] url=stringNext.split("/");
            String nextLikes=url[url.length-2]+"/"+url[url.length-1];
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    nextLikes,
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            JSONObject likes=response.getJSONObject();
                            try {
                                List<Like> likesList = Like.likesList(likes.getJSONArray("data"));
                                SingletonUser.getUser().likesList.addAll(likesList);
                                getLikes(likes);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            };

                        }
                    }
            ).executeAsync();
        }else{
            updateUser(SingletonUser.getUser(),false);
        }

    }
    public void  saveUser(final User user) {
        user.setDefaultInterests();
        SingletonUser.setUser(user);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("users").child(user.Uid).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgressDialog();
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    Log.d(TAG, "Data saved successfully.");

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(BaseActivity.this, R.style.AppThemeDialog);
                    builder.setTitle("Todo listo");
                    builder.setMessage("Bienvenido a LinkUp!");
                    builder.setIcon(R.drawable.ic_done_black_24dp);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(TAG, "Exito");
                            SingletonUser.setUser(user);
                            startMainActivity();
                        }

                    });
                    if(!isFinishing()) {

                        builder.show();
                    }
                }
            }
        });
    }



    protected void setUserProfile(final SerializableUser user, String likes, final Boolean setMapButton){
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(user.getName()+", "+user.getAge());
        CarouselView carouselView = (CarouselView) findViewById(R.id.carouselView);
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Photo>>(){}.getType();
        final List<Photo> profilePhotosList=gson.fromJson(user.getProfilePhotosList(),listType);
        carouselView.setPageCount(profilePhotosList.size());
        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Picasso.with(getApplicationContext()).load(profilePhotosList.get(position).url).fit().centerCrop().into(imageView);

            }
        };
        carouselView.setImageListener(imageListener);
        if(!user.getWork().trim().isEmpty()) {
            TextView proffesionText = (TextView) findViewById(R.id.proffesion_text);
            proffesionText.setText(user.getWork());
        }else{
            LinearLayout workLayout=(LinearLayout) findViewById(R.id.proffesion_Layout);
            workLayout.setVisibility(View.GONE);
        }
        if(!user.getFormation().trim().isEmpty()) {
            TextView centerStudyText = (TextView) findViewById(R.id.center_study_text);
            centerStudyText.setText(user.getFormation());
        }else{
            LinearLayout education_Layout=(LinearLayout) findViewById(R.id.education_Layout);
            education_Layout.setVisibility(View.GONE);
        }
        if(!likes.trim().isEmpty()) {
            TextView like_text = (TextView) findViewById(R.id.like_text);
            like_text.setText(likes);
        }else{
            LinearLayout likes_Layout=(LinearLayout) findViewById(R.id.likes_Layout);
            likes_Layout.setVisibility(View.GONE);
        }
        if(!user.getCommonLikes().trim().isEmpty()){
            TextView commonLTextView = (TextView) findViewById(R.id.commonLikes_text);
            commonLTextView.setText(user.getCommonLikes());
        }else{
            LinearLayout commonLikesLayout=(LinearLayout) findViewById(R.id.commonLikes_Layout);
            commonLikesLayout.setVisibility(View.GONE);
        }
        if((!user.getDistance().equals("-1"))&&!user.getDistance().isEmpty()){
            TextView distanceTextView = (TextView) findViewById(R.id.distance_text);
            distanceTextView.setText(user.getDistanceString());
        }else{
            LinearLayout distance_Layout=(LinearLayout) findViewById(R.id.distance_Layout);
            distance_Layout.setVisibility(View.GONE);
        }
        if(!user.getAboutMe().trim().isEmpty()) {
            TextView about_me_text = (TextView) findViewById(R.id.about_me_text);
            about_me_text.setText(user.getAboutMe());
        }else{
            LinearLayout aboutMe_Layout=(LinearLayout) findViewById(R.id.aboutMe_Layout);
            aboutMe_Layout.setVisibility(View.GONE);
        }
        Button btnMap = (Button) findViewById(R.id.btn_map);
        if(setMapButton){
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   goToMapActivity(user.getDistance());
                }
            });
        }else{
            btnMap.setVisibility(View.GONE);
        }
    }

    protected void blockUser(final String id, final String name) {
        final User user=SingletonUser.getUser();
        AlertDialog.Builder builder =
                new AlertDialog.Builder(BaseActivity.this, R.style.AppThemeDialog);
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_block, null);
        builder.setView(mView);
        Button blockButton=(Button) mView.findViewById(R.id.btn_blockDialog);
        TextView textBlock=(TextView) mView.findViewById(R.id.dialog_blockText);
        textBlock.setText("Si bloqueas a "+name+" no podras hablar, ni ponerte en contacto.\n¿Deseas bloquear a "+name+ "?");
        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBase.saveBlock(user.Uid,id);
                AlertDialog.Builder builder=new AlertDialog.Builder(BaseActivity.this).setTitle("Exito").setMessage("Has bloqueado a "+name+".");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        goToBlockActivity();
                    }

                }).show();
            }
        });
        builder.show();

    }

    protected void deleteLink(final String id, final String name) {
        final User user=SingletonUser.getUser();
        AlertDialog.Builder builder =
                new AlertDialog.Builder(BaseActivity.this, R.style.AppThemeDialog);
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_block, null);
        builder.setView(mView);
        Button blockButton=(Button) mView.findViewById(R.id.btn_blockDialog);
        TextView textBlock=(TextView) mView.findViewById(R.id.dialog_blockText);

        textBlock.setText("Estás seguro que deseas eliminar tu link con "+name+" ?");
        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBase.saveDeleteLink(user.Uid,id);
                AlertDialog.Builder builder=new AlertDialog.Builder(BaseActivity.this).setTitle("Exito").setMessage("Has bloqueado a "+name+".");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        goToBlockActivity();
                    }

                }).show();
            }
        });
        builder.show();
    }

    protected void goToBlockActivity(){
        startMainActivity();
    }

    protected void reportUser(final String id, final String name) {
        final User user=SingletonUser.getUser();
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(BaseActivity.this);
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_report, null);
        builder.setView(mView);
        final AlertDialog alertDialog = builder.create();
        final EditText input = (EditText)mView.findViewById(R.id.dialog_reportInput);
        TextView textBlock=(TextView) mView.findViewById(R.id.dialog_reportText);
        textBlock.setHint("Razones de la denuncia");
        textBlock.setText("Explique las razones por la cual quieres denunciar a "+name+", esto puede resultar en que su cuenta sea eliminada de la aplicacion.");
        Button reportButton=(Button) mView.findViewById(R.id.btn_reportDialog);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report report =new Report(user.Uid,id,input.getText().toString());
                DataBase.saveReport(report);
                alertDialog.dismiss();
                AlertDialog.Builder builder=new AlertDialog.Builder(BaseActivity.this).setTitle("Denuncia Procesada").setMessage("Tu denuncia ha sido enviada y pronto sera revisada por nuestros sexys administradores.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                    }

                }).show();
            }
        });
        alertDialog.show();
    }

    private  void goToMapActivity(String distance){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("distance",distance);
        startActivity(intent);
    }
    public  void updateUser(final User user, final Boolean showMessage){
        if(showMessage) {
            showProgressDialog();
        }
        Map<String, Object> map = user.toMap();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();


        ref.child("users").child(user.Uid).updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    if(showMessage) {
                        Log.d(TAG, "Data saved successfully.");

                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(BaseActivity.this, R.style.AppThemeDialog);
                        builder.setTitle(getResources().getString(R.string.edit_success_title));
                        builder.setMessage(getResources().getString(R.string.edit_success_message));
                        builder.setIcon(R.drawable.ic_check_white_24dp);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                Log.d(TAG, "Exito");
                                onBackPressed();
                            }

                        });
                        if (!isFinishing()) {

                            builder.show();
                        }

                    }
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

    public void startMissingInformationActivityForResult(){
        Intent intent = new Intent(this, FillMissingInformationActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String gender =data.getStringExtra("result");
                User user = SingletonUser.getUser();
                user.gender = gender;
                showProgressDialog();
                saveUser(user);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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

    public void deleteAccount() {
        showProgressDialog();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    LoginManager.getInstance().logOut();
                                    hideProgressDialog();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);

                                }
                            }


                        });
            }


        });
    }
}

