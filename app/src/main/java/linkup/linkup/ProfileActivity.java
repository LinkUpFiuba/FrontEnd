package linkup.linkup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import linkup.linkup.model.SerializableUser;

public class ProfileActivity extends BaseActivity {

    public static final String LIKE = "1";
    public static final String SUPER_LIKE = "2";
    public static final String DONT_LIKE = "3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(250);
        getWindow().setSharedElementEnterTransition(bounds);

        SerializableUser user = (SerializableUser) getIntent().getParcelableExtra("user");

        toolbarLayout.setTitle(user.getName()+", "+user.getAge());

        ImageView imageView1 = (ImageView) findViewById(R.id.user_image);
        Picasso.with(this).load(user.getPhotoURL()).fit().centerCrop().into(imageView1);

        setUserProfile(user,user.getLikes(),true);

        FloatingActionButton fabLike = (FloatingActionButton)findViewById(R.id.fabLikeProfile);
        fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", LIKE);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        FloatingActionButton fabSuperLike = (FloatingActionButton)findViewById(R.id.fabSuperLikeProfile);
        fabSuperLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent1 = new Intent();
                returnIntent1.putExtra("result", SUPER_LIKE);
                setResult(Activity.RESULT_OK,returnIntent1);
                finish();
            }
        });
        FloatingActionButton fabDontLike = (FloatingActionButton)findViewById(R.id.fabDontLikeProfile);
        fabDontLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent2 = new Intent();
                returnIntent2.putExtra("result", DONT_LIKE);
                setResult(Activity.RESULT_OK,returnIntent2);
                finish();
            }
        });
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
    protected void goToBlockActivity() {
        Intent returnIntent2 = new Intent();
        returnIntent2.putExtra("result", DONT_LIKE);
        setResult(Activity.RESULT_OK,returnIntent2);
        finish();
    }
}
