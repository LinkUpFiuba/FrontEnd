package linkup.linkup.model;

import com.google.firebase.database.Exclude;
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

public class Photo implements Serializable {
    public String url;
    private Photo(){}
    public static List<Photo> photoList(JSONArray photoArray) throws JSONException {
        List<Photo> photoList =new ArrayList<Photo>();
        for(int i=0; i<photoArray.length();i++){
            Photo photo=new Photo();
            JSONObject photoObject = photoArray.getJSONObject(i);
            if(photoObject.has("url")){
                photo.url=photoObject.getString("url");
                photoList.add(photo);
            }
        }
        return photoList;
    }
    public Photo(String id){this.url=id;}

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Photo)) {
            return false;
        }
        boolean equals= (((Photo) o).url.equals(url));
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

}
