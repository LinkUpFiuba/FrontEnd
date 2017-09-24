package linkup.linkup.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * Created by german on 9/9/2017.
 */

public class SerializableUser implements Parcelable {

    String id, fbId, name,aboutMe, birthday, genre,work, formation, likes ,photoURL,commonLikes,distance;

    public SerializableUser(String id, String fbId, String name,String aboutMe, String birthday, String genre, String work, String formation, String likes, String photoURL,String commonLikes,String distance) {
        this.id = id;
        this.fbId = fbId;
        this.name = name;
        this.aboutMe = aboutMe;
        this.birthday = birthday;
        this.genre = genre;
        this.work = work;
        this.formation = formation;
        this.likes = likes;
        this.photoURL = photoURL;
        this.commonLikes=commonLikes;
        this.distance=distance;
    }

    public String getId() {

        return id;
    }

    public String getDistance(){return distance;}
    public String getDistanceString(){return "A "+distance+" km de distancia";}
    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getAge(){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime dt = formatter.parseDateTime(birthday);
        DateTime today= DateTime.now();
        Period period=new Period(dt,today);
        int years= period.getYears();
        String age = String.valueOf(years);
        return age;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public String getLikes() {
        return likes;
    }

    public String getCommonLikes() {
        return commonLikes;
    }

    public void setLikes(String interests) {
        this.likes = interests;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }


    public String getFbProfileImageUrl() {
        if (this.hasFbId()) {
            return "http://graph.facebook.com/ger.funebrero/picture?type=large";
        }
        return "";
    }

    protected SerializableUser(Parcel in) {
        id = in.readString();
        fbId = in.readString();
        name = in.readString();
        aboutMe = in.readString();
        birthday = in.readString();
        genre = in.readString();
        work = in.readString();
        formation = in.readString();
        likes = in.readString();
        photoURL = in.readString();
        commonLikes =in.readString();
        distance=in.readString();
    }

    public static final Creator<SerializableUser> CREATOR = new Creator<SerializableUser>() {
        @Override
        public SerializableUser createFromParcel(Parcel in) {
            return new SerializableUser(in);
        }

        @Override
        public SerializableUser[] newArray(int size) {
            return new SerializableUser[size];
        }
    };


    public boolean hasFbId() {
        return fbId != null && !fbId.isEmpty();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.fbId);
        dest.writeString(this.name);
        dest.writeString(this.aboutMe);
        dest.writeString(this.birthday);
        dest.writeString(this.genre);
        dest.writeString(this.work);
        dest.writeString(this.formation);
        dest.writeString(this.likes);
        dest.writeString(this.photoURL);
        dest.writeString(this.commonLikes);

        dest.writeString(this.distance);

    }
}
