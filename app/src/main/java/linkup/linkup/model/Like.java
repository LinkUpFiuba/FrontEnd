package linkup.linkup.model;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by andres on 9/5/17.
 */
@IgnoreExtraProperties

public class Like implements Serializable{
    public String name;
    public String id;
    public Like(){}

    public static List<Like> likesList(JSONArray likesArray) throws JSONException {
        List<Like> likeList =new ArrayList<Like>();
        for(int i=0; i<likesArray.length();i++){
            Like like=new Like();
            JSONObject likeObject = likesArray.getJSONObject(i);
            like.name=likeObject.getString("name");
            like.id=likeObject.getString("id");
            likeList.add(like);
        }
        return likeList;
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Like){
            Like otherLike=(Like)o;
            if( this.id.equals(otherLike.id)){
                return true;
            }
            return false;
        }
        return false;
    }
    @Override
    public  int hashCode(){
        return Objects.hash(id,name);
    }
}
