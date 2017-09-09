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
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import linkup.linkup.Utils.DataBase;


/**
 * Created by andres on 9/3/17.
 */

@IgnoreExtraProperties

public class User {
    public String Uid;
    public String photoUrl;
    public String name;
    public String email;
    public String birthday;
    public String age;
    public String gender;
    public Range range;
    public List<Like> likesList;
    public List<Education> educationList;
    public List<Work>workList;
    public String education;
    public String work;
    public String aboutMe;

    public boolean invisibleMode;
    public boolean linkUpPlus;
    public Interests interests;
    public List<Photo> photoList;

    public User(){

    }

    public User(FirebaseUser firebaseUser){
        invisibleMode=false;
        linkUpPlus=false;
        Uid =firebaseUser.getUid();
        photoUrl = firebaseUser.getPhotoUrl().toString();
        name = firebaseUser.getDisplayName();
        email =firebaseUser.getEmail();
        interests=new Interests();
        final User user=this;
        aboutMe="";
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            if(object.has("gender")) {
                                gender = object.getString("gender");
                            }else {
                                gender="";
                            }
                            if(object.has("birthday")){
                                birthday=object.getString("birthday");
                                DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                                DateTime dt = formatter.parseDateTime(birthday);
                                DateTime today= DateTime.now();
                                Period period=new Period(dt,today);
                                int years= period.getYears();
                                age=String.valueOf(years);
                                range=new Range(years);
                            }else {
                                birthday="";
                                age="18";
                            }
                            if(object.has("education")) {
                                educationList = Education.educationList(object.getJSONArray("education"));
                                education=educationList.get(educationList.size()-1).name;
                            }else {
                                education="";
                            }
                            if(object.has("likes")) {
                                likesList = Like.likesList(object.getJSONObject("likes").getJSONArray("data"));
                            }
                            if(object.has("work")) {
                                workList=Work.workList(object.getJSONArray("work"));
                                work=workList.get(workList.size()-1).name;
                            }else{
                                work="";
                                workList=new ArrayList<Work>();
                            }

                            if(object.has("photos")) {
                                photoList=Photo.photoList(object.getJSONObject("photos").getJSONArray("data"));
                            }else {
                                photoList=new ArrayList<Photo>();
                            }

                        }catch (JSONException e){
                            Log.d("User",e.toString());
                        }
                        DataBase.saveUser(user);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "gender,birthday,education,likes,work,photos,id");
        request.setParameters(parameters);
        request.executeAsync();
    }
    @Exclude
    public String getLikesString(){
        String likesString="";
        if(likesList == null) return "";
        for (Like like: likesList ){
            likesString+=(like.name+", ");
        }
        return likesString;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Uid",Uid);
        result.put("photoUrl",photoUrl);
        result.put("name",name);
        result.put("email",email);

        result.put("birthday",birthday);
        result.put("age",age);
        result.put("gender",gender);

        result.put("range",range);

        result.put("likesList",likesList);


        result.put("educationList",educationList);

        result.put("workList",workList);

        result.put("education",education);

        result.put("work",work);
        result.put("aboutMe",aboutMe);

        result.put("invisibleMode",invisibleMode);

        result.put("linkUpPlus",linkUpPlus);

        result.put("interests",interests);
        result.put("photoList",photoList);


        return result;
    }
}
