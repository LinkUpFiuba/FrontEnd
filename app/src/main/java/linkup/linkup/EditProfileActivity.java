package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import linkup.linkup.Utils.DataBase;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.make_changes_profile);
        View makeChangesButton = (View) item.getActionView();
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                User user = SingletonUser.get();
                boolean changed = false;
                TextView proffesionText = (TextView) findViewById(R.id.editable_proffesionText);

                if (proffesionText.getText() != user.work) {
                    user.work = proffesionText.getText().toString();
                    changed = true;
                }
                TextView centerStudyText = (TextView) findViewById(R.id.editable_centerStudyText);
                if (centerStudyText.getText() != user.education) {
                    user.education = centerStudyText.getText().toString();
                    changed = true;
                }
                TextView about_me_text = (TextView) findViewById(R.id.editable_about_me_text);
                if (about_me_text.getText() != user.aboutMe) {
                    user.aboutMe = about_me_text.getText().toString();
                    changed = true;
                }
                if (changed) {
                    DataBase.updateUser(user);
                }
                onBackPressed();

                return true;
            }
        });
        return true;


    }


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
        TextView proffesionText = (TextView) findViewById(R.id.editable_proffesionText);
        if(!user.work.isEmpty()) {
            proffesionText.setText(user.work);
        }else{
            proffesionText.setHint("Donde trabajas?");
        }
        TextView centerStudyText = (TextView) findViewById(R.id.editable_centerStudyText);
        if(!user.work.isEmpty()) {
            centerStudyText.setText(user.education);
        }else{
            centerStudyText.setHint("Donde estudias/estudiaste?");
        }

        TextView about_me_text = (TextView) findViewById(R.id.editable_about_me_text);
        if(!user.aboutMe.isEmpty()) {
            about_me_text.setText(user.aboutMe);
        }else{
            about_me_text.setHint("Cuenta algo sobre ti");
        }

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
