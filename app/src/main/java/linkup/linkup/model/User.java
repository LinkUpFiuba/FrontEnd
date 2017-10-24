package linkup.linkup.model;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.Gson;

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
    public static final int DISTANCE_OWN_USER = -1;
    public static final int DEFAULT_SUPER_LINKS = 5;
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
    public List<Photo> profilePhotosList = new ArrayList<>();
    public UserLocation location =new UserLocation();
    public int availableSuperlinks = DEFAULT_SUPER_LINKS;

    @Exclude
    public int distance= DISTANCE_OWN_USER;
    @Exclude
    public List<Like> commonLikes=new ArrayList<>();
    public User(){

    }
    @Exclude
    public SerializableUser getSerializableUser(){
        return new SerializableUser(this.Uid,this.Uid,this.name,this.aboutMe,this.birthday,this.gender,this.work,this.education, getNotCommonLikesString(),this.photoUrl,getCommonLikesString(),String.valueOf( this.distance),new Gson().toJson(this.profilePhotosList));
    }
    public User(FirebaseUser firebaseUser){

        invisibleMode=false;
        linkUpPlus=false;
        Uid =firebaseUser.getUid();
        photoUrl = "https://graph.facebook.com/"+getFacebookId()+"/picture?height=400";
        profilePhotosList.add(new Photo(photoUrl));
        name = firebaseUser.getDisplayName();
        email =firebaseUser.getEmail();
        interests=new Interests();
        aboutMe="";
    }
    @Exclude
    public Link link(User linkedUser){
        return  Link.createLink(this,linkedUser);
    }
    @Exclude
    public boolean hasAvailableSuperLinks(){
        return availableSuperlinks>0;
    }
    @Exclude
    public Link superLink(User linkedUser){
        this.availableSuperlinks-=1;
        return  Link.createSuperLink(this,linkedUser);
    }

    @Exclude
    public String getNotCommonLikesString(){
        String likesString="";
        if(likesList == null || likesList.size()==0) return "";
        likesList.removeAll(commonLikes);
        for (int i=0; i<likesList.size()-1;i++ ){
                likesString += (likesList.get(i).name + ", ");
        }
        likesString += (likesList.get(likesList.size()-1).name + ".");
        return likesString;
    }
    @Exclude
    public String getCommonLikesString(){
        String likesString="";
        if(commonLikes == null||commonLikes.isEmpty()) return "";
        for (int i=0; i<commonLikes.size()-1;i++ ){
            likesString += (commonLikes.get(i).name + ", ");
        }
        likesString += (commonLikes.get(commonLikes.size()-1).name + ".");
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
        result.put("profilePhotosList",profilePhotosList );

        result.put("getNotifications",getNotifications);
        result.put("maxDistance",maxDistance);
        result.put("location", location);
        result.put("tokenFCM",tokenFCM);
        result.put("availableSuperlinks",availableSuperlinks);

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
