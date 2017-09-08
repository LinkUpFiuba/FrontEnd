package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class MyProfileActivity extends AppCompatActivity {
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
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUserProfile();

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
    private void setUserProfile(){
        User user = SingletonUser.get();
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(user.name+","+user.age);
        TextView proffesionText = (TextView) findViewById(R.id.proffesion_text);
        proffesionText.setText(user.work);
        TextView centerStudyText = (TextView) findViewById(R.id.center_study_text);
        centerStudyText.setText(user.education);
        TextView like_text = (TextView) findViewById(R.id.like_text);
        like_text.setText(user.getLikesString());
        TextView about_me_text = (TextView) findViewById(R.id.about_me_text);
        about_me_text.setText(user.aboutMe);
    }
    public void startEditProfileActivity(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}
