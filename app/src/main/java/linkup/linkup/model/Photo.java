package linkup.linkup.model;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andres on 9/5/17.
 */
@IgnoreExtraProperties

public class Photo {
    public String id;
    private Photo(){}
    public static List<Photo> photoList(JSONArray photoArray) throws JSONException {
        List<Photo> photoList =new ArrayList<Photo>();
        for(int i=0; i<photoArray.length();i++){
            Photo photo=new Photo();
            JSONObject photoObject = photoArray.getJSONObject(i);
            photo.id=photoObject.getString("id");
            photoList.add(photo);
        }
        return photoList;
    }
}
