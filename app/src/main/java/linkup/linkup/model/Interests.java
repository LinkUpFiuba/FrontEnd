package linkup.linkup.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andres on 9/5/17.
 */
@IgnoreExtraProperties

public class Interests {
    public Interests(){
        male =false;
        female =false;
        friends=false;
    }
    public boolean male, female,friends;
    public boolean searchesMen(){
        return male;
    }
    public boolean searchesWomen(){
        return female;
    }
    public boolean searchesFriends(){
        return friends;
    }
    public void setSearchesMen(boolean searchesMen){
        if(searchesMen) {
            male = true;
            friends = false;
        }else {
            male =false;
        }

    }

    public void setSearchesWomen(boolean searchesWomen){
        if(searchesWomen) {
            female = true;
            friends = false;
        }else {
            female =false;
        }
    }
    public void setSearchesFriends(boolean searchesFriends){
        if(searchesFriends) {
            female = false;
            male =false;
            friends = true;
        }else {
            friends=false;
        }
    }
}
