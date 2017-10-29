package linkup.linkup;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import linkup.linkup.model.Photo;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class PickPhotoActivity extends EditProfileActivity {
    protected ArrayList<Photo> selectedPhotos = new ArrayList<>();
    protected ArrayList<Photo> availablePhotos = new ArrayList<>();
    protected Map<Photo,ImageView> selectedImageViews=new HashMap<Photo,ImageView>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPickPhoto);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getResources().getString(R.string.nav_item_pick_photo));
        }
        setSelectedPhotos();
        getProfileAlbumId();

    }

    protected void setSelectedPhotos(){
        User user= SingletonUser.getUser();
        selectedPhotos.addAll((ArrayList<Photo>) user.profilePhotosList);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setClickOnSave(menu);
        return true;
    }
    @Override
    protected void checkForChange() {
        User user= SingletonUser.getUser();
        changed=(!selectedPhotos.equals(user.profilePhotosList));
    }
    @Override
    protected void changeUser(){
        User user = SingletonUser.getUser();
        user.profilePhotosList=selectedPhotos;

    }
    @Override
    protected void getBackToPreviousActivity(){
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        getBackToPreviousActivity();
    }

    protected int getMaxPhotos(){
        return 6;
    }
    protected void selectPhoto(Photo clickedPhoto, ImageView imageView){
        if(!selectedPhotos.contains(clickedPhoto)){
            if ((selectedPhotos.size() <= getMaxPhotos())){
                selectedPhotos.add(clickedPhoto);
                imageView.setColorFilter(Color.argb(150, 0, 0, 150));
            }
        }else {
            selectedPhotos.remove(clickedPhoto);
            imageView.setColorFilter(Color.argb(0, 0, 0, 0));

        }
    }
    protected void setPhotoInGrid(String imageUrl) {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridlayout);
        final ImageView imageView = new ImageView(this);

        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.rightMargin = 15;
        param.leftMargin = 15;
        param.topMargin = 15;
        param.bottomMargin = 15;
        param.width = (gridLayout.getWidth() / gridLayout.getColumnCount()) - param.rightMargin - param.leftMargin;
        param.setGravity(Gravity.CENTER);
        param.height = param.width;

        imageView.setLayoutParams(param);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.availablePhotos.add(new Photo(imageUrl));
        imageView.setTag(R.id.TAG,this.availablePhotos.size()-1);
        Picasso.with(this).load(imageUrl).fit().centerCrop().into(imageView);
        gridLayout.addView(imageView);
        if(selectedPhotos.contains(new Photo(imageUrl))){
            imageView.setColorFilter(Color.argb(150, 0, 0, 150));
            selectedImageViews.put(new Photo(imageUrl),imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag(R.id.TAG);
                Photo clickedPhoto=availablePhotos.get(position);
                selectPhoto(clickedPhoto,imageView);

            }
        });

    }
    protected void getProfileAlbumId(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        String albumID = null;
                        try {
                            JSONObject json = response.getJSONObject();
                            JSONArray jarray = json.getJSONArray("data");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject oneAlbum = jarray.getJSONObject(i);
                                //get albums id
                                if (oneAlbum.getString("name").equals("Profile Pictures")) {
                                    albumID = oneAlbum.getString("id");
                                    getPhotoAlbumById(albumID);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
    }

    protected void getPhotoAlbumById(String albumId) {


                String params="/"+albumId+"/photos";
                new GraphRequest(AccessToken.getCurrentAccessToken(), params,null , HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                if (response != null) {
                                    try {
                                        JSONObject data = response.getJSONObject();
                                        JSONArray photosUrls = data.getJSONArray("data");
                                        for (int i = 0; i < photosUrls.length() & i < 15; i++) {
                                            String photoId = photosUrls.getJSONObject(i).getString("id");
                                            getPhotoBy(photoId);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).executeAsync();
            }


    protected void getPhotoBy(final String id) {
                Bundle params = new Bundle();
                params.putString("fields", "images");
                new GraphRequest(AccessToken.getCurrentAccessToken(), id, params, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                if (response != null) {
                                    try {
                                        JSONObject data = response.getJSONObject();
                                        final JSONArray imagesData = data.getJSONArray("images");
                                        final String imageUrl = imagesData.getJSONObject(0).getString("source");
                                        setPhotoInGrid(imageUrl);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).executeAsync();
            }
    }