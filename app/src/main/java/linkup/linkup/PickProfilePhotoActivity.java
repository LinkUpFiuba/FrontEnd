package linkup.linkup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import linkup.linkup.model.Photo;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class PickProfilePhotoActivity extends PickPhotoActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPickPhoto);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getResources().getString(R.string.nav_item_pick_profile_photo));
        }

    }
    protected int getMaxPhotos(){
        return 1;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setClickOnSave(menu);
        return true;
    }
    @Override
    protected void checkForChange() {
        User user= SingletonUser.getUser();
        changed=(!selectedPhotos.get(0).url.equals(user.photoUrl));
    }
    @Override
    protected void changeUser(){
        User user = SingletonUser.getUser();
        user.photoUrl=selectedPhotos.get(0).url;
    }
    @Override
    protected void setSelectedPhotos(){
        selectedPhotos.add(new Photo(SingletonUser.getUser().photoUrl));
    }
    @Override
    protected void selectPhoto(Photo clickedPhoto, ImageView imageView) {
        for(ImageView imageViewIter:selectedImageViews.values()){
            imageViewIter.setColorFilter(Color.argb(0, 0, 0, 0));
        }
        selectedImageViews.clear();
        selectedPhotos.clear();
        selectedImageViews.put(clickedPhoto,imageView);
        selectedPhotos.add(clickedPhoto);
        imageView.setColorFilter(Color.argb(150, 0, 0, 150));

    }
}