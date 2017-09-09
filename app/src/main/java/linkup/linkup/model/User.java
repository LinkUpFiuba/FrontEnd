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

import java.io.Serializable;
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
    public String Uid = "";
    public String photoUrl = "";
    public String name = "" ;
    public String email = "";
    public String birthday = "";
    public String age = "";
    public String gender = "";
    public Range range = new Range(18);
    public List<Like> likesList = new ArrayList<>();
    public List<Education> educationList= new ArrayList<>();
    public List<Work>workList = new ArrayList<>();
    public String education = "";
    public String work = "";
    public String aboutMe = "";

    public boolean invisibleMode = false;
    public boolean linkUpPlus = false;
    public Interests interests = new Interests();
    public List<Photo> photoList = new ArrayList<>();

    public User(){

    }
    public SerializableUser getSerializableUser(){
        return new SerializableUser(this.Uid,this.Uid,this.name,this.aboutMe,this.birthday,this.gender,this.work,this.education,getLikesString(),this.photoUrl);
    }
    public User(FirebaseUser firebaseUser){
        invisibleMode=false;
        linkUpPlus=false;
        Uid =firebaseUser.getUid();
        photoUrl = firebaseUser.getPhotoUrl().toString();
        name = firebaseUser.getDisplayName();
        email =firebaseUser.getEmail();
        interests=new Interests();
        aboutMe="";
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
