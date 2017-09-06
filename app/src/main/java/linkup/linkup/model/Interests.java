package linkup.linkup.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andres on 9/5/17.
 */
@IgnoreExtraProperties

public class Interests {
    public Interests(){}
    private boolean men,women,friends;
    public boolean searchesMen(){
        return men;
    }
    public boolean searchesWomen(){
        return men;
    }
    public boolean searchesFriends(){
        return men;
    }
    public void setSearchesMen(boolean searchesMen){
        if(searchesMen) {
            men = true;
            friends = false;
        }else {
            men=false;
        }

    }

    public void setSearchesWomen(boolean searchesWomen){
        if(searchesWomen) {
            women = true;
            friends = false;
        }else {
            women=false;
        }
    }
    public void setSearchesFriends(boolean searchesFriends){
        if(searchesFriends) {
            women = false;
            men=false;
            friends = true;
        }else {
            friends=false;
        }
    }
}
