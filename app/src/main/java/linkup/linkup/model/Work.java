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

public class Work {
    public String name;
    public String id;
    public Work(){}
    public static List<Work> workList(JSONArray worksArray) throws JSONException {
        List<Work> workList =new ArrayList<Work>();
        for(int i=0; i<worksArray.length();i++){
            Work work=new Work();
            JSONObject workObject = worksArray.getJSONObject(i);
            JSONObject employerObject=workObject.getJSONObject("employer");
            work.name=employerObject.getString("id");
            work.id=employerObject.getString("name");
            workList.add(work);
        }
        return workList;
    }
}
