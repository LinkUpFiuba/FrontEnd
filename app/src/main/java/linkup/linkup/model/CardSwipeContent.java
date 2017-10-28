package linkup.linkup.model;

import com.google.firebase.database.Exclude;

/**
 * Created by german on 10/15/2017.
 */

public class CardSwipeContent {
    public static final String CANDIDATE = "CANDIDATE";
    public static final String AD = "AD";

    private Advertisement ad;
    private User user;
    String type = CANDIDATE;

    public CardSwipeContent(){

    }
    public CardSwipeContent(String type) {
        this.type = type;
    }

    public Advertisement getAd() {
        return ad;
    }

    public void setAd(Advertisement ad) {
        this.ad = ad;
        this.type = AD;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Exclude
    public boolean hasAd() {
        return (this.getAd() != null);
    }

    @Exclude
    public boolean hasUser() {
        return (this.getUser() != null);
    }
}
