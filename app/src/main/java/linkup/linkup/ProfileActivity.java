package linkup.linkup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;

import linkup.linkup.Utils.LikeObserver;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

import static android.R.attr.id;

public class ProfileActivity extends BaseActivity {

    public static final String LIKE = "1";
    public static final String SUPER_LIKE = "2";
    public static final String DONT_LIKE = "3";
    private static final String TAG = "ProfileActivity";
    SerializableUser user;

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

        user = (SerializableUser) getIntent().getParcelableExtra("user");

        toolbarLayout.setTitle(user.getName()+", "+user.getAge());

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
                User myUser = SingletonUser.getUser();
                if(myUser.hasAvailableSuperLinks()){
                    Intent returnIntent1 = new Intent();
                    returnIntent1.putExtra("result", SUPER_LIKE);
                    setResult(Activity.RESULT_OK,returnIntent1);
                    finish();
                }else{
                    if(myUser.linkUpPlus){
                        Toast.makeText(v.getContext(), "No tienes más Superlinks, a partir de mañana tendrás más. ", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(v.getContext(), "No tienes más Superlinks, si quieres disponer de más puedes conseguir LinkUp Plus.", Toast.LENGTH_LONG).show();

                    }
                }

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

            case R.id.denuncia:
                reportUser(user.getId(),user.getName());
                break;

            case R.id.bloqueo:
                blockUser(user.getId(),user.getName());
                break;

            case R.id.share:
                shareProfile(user.getId());
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareProfile(String id) {
        Uri link = buildDeepLink(id);
        shareShortLink(link);
    }


    @Override
    protected void goToBlockActivity() {
        Intent returnIntent2 = new Intent();
        returnIntent2.putExtra("result", DONT_LIKE);
        setResult(Activity.RESULT_OK,returnIntent2);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public Uri buildDeepLink(String userId ) {
        String DEEP_LINK_URL = "https://linkupapp.com/" + userId + "/";
        Log.d(TAG,DEEP_LINK_URL);

        final Uri deepLink = Uri.parse(DEEP_LINK_URL);

        String domain = "qa224.app.goo.gl";

        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDynamicLinkDomain(domain)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .build())
                .setLink(deepLink);

        // Build the dynamic link
        DynamicLink link = builder.buildDynamicLink();

        // Return the dynamic link as a URI
        return link.getUri();
    }

    private void shareDeepLink(String deepLink) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
        intent.putExtra(Intent.EXTRA_TEXT,deepLink);

        startActivity(intent);
    }

    private void shareShortLink(Uri deepLink){
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(deepLink)
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            shareDeepLink(shortLink.toString());
                        } else {
                            // Error
                            // ...
                        }
                    }
                });
    }

}
