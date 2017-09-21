package linkup.linkup.model;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by andres on 9/3/17.
 */

@IgnoreExtraProperties

public class User {
    private static final int MAX_DISTANCE_ALLOWED = 100;
    public String Uid = "";
    public String photoUrl = "";
    public String name = "" ;
    public String email = "";
    public String birthday = "";
    public String age = "";
    public String gender = "";
    public String tokenFCM="";
    public Range range = new Range(18);
    public int maxDistance = MAX_DISTANCE_ALLOWED;
    public List<Like> likesList = new ArrayList<>();
    public List<Education> educationList= new ArrayList<>();
    public List<Work>workList = new ArrayList<>();
    public String education = "";
    public String work = "";
    public String aboutMe = "";
    public boolean getNotifications=true;
    public boolean invisibleMode = false;
    public boolean linkUpPlus = false;
    public Interests interests = new Interests();
    public List<Photo> photoList = new ArrayList<>();
    public UserLocation location =new UserLocation();
    @Exclude
    public Float distance;
    @Exclude
    public List<Like> commonLikes;
    public User(){

    }
    @Exclude
    public SerializableUser getSerializableUser(){
        return new SerializableUser(this.Uid,this.Uid,this.name,this.aboutMe,this.birthday,this.gender,this.work,this.education,getLikesString(),this.photoUrl);
    }
    public User(FirebaseUser firebaseUser){

        invisibleMode=false;
        linkUpPlus=false;
        Uid =firebaseUser.getUid();
        photoUrl = "https://graph.facebook.com/"+getFacebookId()+"/picture?height=400";
        name = firebaseUser.getDisplayName();
        email =firebaseUser.getEmail();
        interests=new Interests();
        aboutMe="";
    }
    @Exclude
    public Link link(User linkedUser){
        return new Link(this,linkedUser);
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
    public String getFacebookId(){
        if(Profile.getCurrentProfile()!=null) {
            return Profile.getCurrentProfile().getId();
        }
        return "";
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
        result.put("getNotifications",getNotifications);
        result.put("maxDistance",maxDistance);
        result.put("location", location);
        result.put("tokenFCM",tokenFCM);
        return result;
    }
    @Exclude
    public void setDefaultInterests() {
        if(gender.equalsIgnoreCase("male")){
            interests.setSearchesWomen(true);
        }else{
            interests.setSearchesMen(true);
        }
    }
    @Exclude

    public Unlink unlink(User user) {
        return new Unlink(this,user);
    }
}
