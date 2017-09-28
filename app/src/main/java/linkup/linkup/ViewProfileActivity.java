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

import static linkup.linkup.ProfileActivity.DONT_LIKE;
import static linkup.linkup.ProfileActivity.LIKE;
import static linkup.linkup.ProfileActivity.SUPER_LIKE;

public class ViewProfileActivity extends BaseActivity {

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

        setUserProfile(user,user.getLikes(),false);

        FloatingActionButton fabLike = (FloatingActionButton)findViewById(R.id.fabLikeProfile);
        fabLike.setVisibility(View.GONE);

        FloatingActionButton fabSuperLike = (FloatingActionButton)findViewById(R.id.fabSuperLikeProfile);
        fabSuperLike.setVisibility(View.GONE);

        FloatingActionButton fabDontLike = (FloatingActionButton)findViewById(R.id.fabDontLikeProfile);
        fabDontLike.setVisibility(View.GONE);
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
}
