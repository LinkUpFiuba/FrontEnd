package connections;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import linkup.linkup.LinkFragment;
import linkup.linkup.Utils.HttpClient;
import linkup.linkup.model.Advertisement;
import linkup.linkup.model.CardSwipeContent;
import linkup.linkup.model.Like;
import linkup.linkup.model.Photo;
import linkup.linkup.model.User;

import static android.R.attr.data;

public class GetUsersAsyncTask extends AsyncTask<String, Void, List<CardSwipeContent>>{

    private static final String TAG = "GetUsersAsyncTask";
    private  ViewWithCards viewWithCards;

    public GetUsersAsyncTask (ViewWithCards view){
        this.viewWithCards = view;
    }

    @Override
        protected List<CardSwipeContent> doInBackground(String... params) {
            //String cityCode = params[0];
            try {
                String data = ((new HttpClient()).getUsers());


                try {
                    if(data!=null){
                        Log.d(TAG, data);

                    List<CardSwipeContent> users = getUsers(data);
                 //   Log.d(TAG, users.getUser(0).name);

                    return users;}
                    return new ArrayList<CardSwipeContent>();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            } catch (UnknownHostException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<CardSwipeContent> users) {
            super.onPostExecute(users);
            this.viewWithCards.showCards(users,true);
        }
    public static List<CardSwipeContent> getUsers(String data) throws JSONException {
        List<CardSwipeContent> users = new ArrayList<>();

        JSONArray dataArray = new JSONArray(data);
        for(int i=0; i<dataArray.length();i++) {
            JSONObject jObj = dataArray.getJSONObject(i);

            String type = getString("type",jObj);
             if(type.equals("ad")){
                addAd(users,jObj);
            }else{
                 addUser(users, jObj);
             }

        }
        return users;
    }

    private static void addUser(List<CardSwipeContent> users, JSONObject jObj) throws JSONException {
        User user = new User();
        user.Uid=getString("Uid",jObj);
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

        if(jObj.has("profilePhotosList")){
            JSONArray jArrayLikesList = jObj.getJSONArray("profilePhotosList");
            user.profilePhotosList = Photo.photoList(jArrayLikesList);
        }
        user.gender= getString("gender",jObj);

        user.work = getString("work",jObj);
        user.distance=Math.round( getFloat("distance",jObj));
        if(jObj.has("commonInterests")){
            JSONArray jArrayLikesList = jObj.getJSONArray("commonInterests");
            user.commonLikes = Like.likesList(jArrayLikesList);
        }
        CardSwipeContent cardSwipeContent = new CardSwipeContent(CardSwipeContent.CANDIDATE);
        cardSwipeContent.setUser(user);
        users.add(cardSwipeContent);
    }

    private static void addAd(List<CardSwipeContent> users, JSONObject jObj) throws JSONException {
        Advertisement ad = new Advertisement();
        ad.setUrlImage(getString("image",jObj));

        CardSwipeContent cardSwipeContent = new CardSwipeContent(CardSwipeContent.CANDIDATE);
        cardSwipeContent.setAd(ad);
        users.add(cardSwipeContent);
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        if(jObj.has(tagName)) return jObj.getString(tagName);
        return "";
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }
}
