package linkup.linkup.model;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andres on 9/5/17.
 */

@IgnoreExtraProperties

public class Work implements Serializable {
    public String name;
    public String id;
    public Work(){}
    public static List<Work> workList(JSONArray worksArray) throws JSONException {
        List<Work> workList =new ArrayList<Work>();
        for(int i=0; i<worksArray.length();i++){
            Work work=new Work();
            JSONObject workObject = worksArray.getJSONObject(i);
            JSONObject positionObject=workObject.getJSONObject("position");
            String position=positionObject.getString("name");
            JSONObject employerObject=workObject.getJSONObject("employer");
            String employer=employerObject.getString("name");
            work.name=position+" en "+employer;
            work.id=employerObject.getString("id");
            workList.add(work);
        }
        return workList;
    }
}
