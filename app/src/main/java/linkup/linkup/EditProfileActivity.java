package linkup.linkup;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getResources().getString(R.string.nav_item_edit_profile));
        }
        setUserProfile();

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
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
