package linkup.linkup;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.User;

public class ProfileActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SerializableUser user = (SerializableUser) getIntent().getParcelableExtra("user");

        toolbarLayout.setTitle(user.getName()+", "+user.getAge());

        ImageView imageView1 = (ImageView) findViewById(R.id.user_image);
        Picasso.with(this).load(user.getPhotoURL()).fit().centerCrop().into(imageView1);

        setUserProfile(user,user.getInterests());

        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(250);
        getWindow().setSharedElementEnterTransition(bounds);
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
