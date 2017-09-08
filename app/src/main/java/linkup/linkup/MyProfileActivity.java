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
        toolbarLayout.setTitle("Camiila, 21");

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
    public void startEditProfileActivity(){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}
