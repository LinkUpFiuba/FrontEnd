package linkup.linkup.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by andres on 9/5/17.
 */
@IgnoreExtraProperties

public class Education implements Serializable {
    public String id;
    public String name;
    public String type;
    public String year;
    public Education(){}
    public static List<Education> educationList(JSONArray education_array) throws JSONException {
        List<Education> educationList=new ArrayList<Education>();
        for(int i=0; i<education_array.length();i++){
            JSONObject education_Object = education_array.getJSONObject(i);
            JSONObject school_Object = education_Object.getJSONObject("school");

            Education education=new Education();
            education.id=school_Object.getString("id");
            education.name=school_Object.getString("name");
            education.type=education_Object.getString("type");
            educationList.add(education);
        }
        return  educationList;
    }
}
