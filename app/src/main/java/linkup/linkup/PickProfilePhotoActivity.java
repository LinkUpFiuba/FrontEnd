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

public class PickProfilePhotoActivity extends EditProfileActivity {
    protected ArrayList<Photo> selectedPhotos = new ArrayList<>();
    protected ArrayList<Photo> availablePhotos = new ArrayList<>();

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
        User user= SingletonUser.getUser();
        selectedPhotos.clear();
        availablePhotos.clear();
        for(Photo photo : user.profilePhotosList){
            if(photo.url.equals(user.photoUrl)){
                selectedPhotos.add(photo);
            }
            setPhotoInGrid(photo);
        }
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


    private void setPhotoInGrid(Photo photo) {
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
        this.availablePhotos.add(new Photo(photo.url));
        imageView.setTag(R.id.TAG,this.availablePhotos.size()-1);
        Picasso.with(this).load(photo.url).fit().centerCrop().into(imageView);
        gridLayout.addView(imageView);
        if(selectedPhotos.contains(new Photo(photo.url))){
            imageView.setColorFilter(Color.argb(150, 0, 0, 150));
        }
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag(R.id.TAG);
                Photo clickedPhoto=availablePhotos.get(position);
                if(!selectedPhotos.contains(clickedPhoto)){
                    GridLayout gridLayout = (GridLayout) findViewById(R.id.gridlayout);

                    int count = gridLayout.getChildCount();
                    for(int i = 0 ; i <count ; i++){
                        ImageView child = (ImageView) gridLayout.getChildAt(i);
                        child.setColorFilter(Color.argb(0, 0, 0, 0));
                    }
                    selectedPhotos.clear();
                    selectedPhotos.add(clickedPhoto);
                    imageView.setColorFilter(Color.argb(150, 0, 0, 150));
                }

            }
        });

    }
}