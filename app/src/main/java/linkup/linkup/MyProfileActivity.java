package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.MenuItem;
import android.view.View;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class MyProfileActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditProfileActivity();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        User user=SingletonUser.getUser();
        setUserProfile(user.getSerializableUser(),user.getNotCommonLikesString(),false);

        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(250);
        getWindow().setSharedElementEnterTransition(bounds);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        User user=SingletonUser.getUser();
        setUserProfile(user.getSerializableUser(),user.getNotCommonLikesString(),false);
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

    public void startEditProfileActivity(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}
