package connections;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import linkup.linkup.LinkFragment;
import linkup.linkup.Utils.HttpClient;
import linkup.linkup.model.Like;
import linkup.linkup.model.Photo;
import linkup.linkup.model.User;

import static android.R.attr.data;

public class GetUsersAsyncTask extends AsyncTask<String, Void, List<User>>{

    private static final String TAG = "GetUsersAsyncTask";
    private  ViewWithCards viewWithCards;

    public GetUsersAsyncTask (ViewWithCards view){
        this.viewWithCards = view;
    }

    @Override
        protected List<User> doInBackground(String... params) {
            //String cityCode = params[0];
            try {
                String data = ((new HttpClient()).getUsers());
//                Log.d(TAG, data);

                try {
                    if(data!=null){
                    List<User> users = getUsers(data);
                 //   Log.d(TAG, users.getUser(0).name);

                    return users;}
                    return new ArrayList<User>();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            } catch (UnknownHostException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            this.viewWithCards.showCards(users,true);
        }
    public static List<User> getUsers(String data) throws JSONException {
        List<User> users = new ArrayList<>();

        JSONArray dataArray = new JSONArray(data);
        for(int i=0; i<dataArray.length();i++) {
            JSONObject jObj = dataArray.getJSONObject(i);
            User user = new User();
            user.name = getString("name",jObj);
            user.aboutMe = getString("aboutMe",jObj);
            user.age = getString("age",jObj);
            user.birthday = getString("birthday",jObj);
            user.education = getString("education",jObj);
            if(jObj.has("likesList")){
                JSONArray jArrayLikesList = jObj.getJSONArray("likesList");
                user.likesList = Like.likesList(jArrayLikesList);
            }
            user.photoUrl = getString("photoUrl",jObj);

            if(jObj.has("photoList")){
                JSONArray jArrayLikesList = jObj.getJSONArray("photoList");
                user.photoList = Photo.photoList(jArrayLikesList);
            }

            user.work = getString("work",jObj);
            users.add(user);
        }


        return users;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        if(jObj.has(tagName)) return jObj.getString(tagName);
        return "";
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }
}
