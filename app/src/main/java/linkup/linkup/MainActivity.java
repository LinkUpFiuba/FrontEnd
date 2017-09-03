package linkup.linkup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity  {

    public static final String CHATS_FRAGMENT = "CHATS_FRAGMENT";
    public static final String LINK_FRAGMENT = "LINK_FRAGMENT";
    public static final String CONFIGURATIONS_FRAGMENT = "CONFIGURATIONS_FRAGMENT";

    private FirebaseAuth mAuth;
    private ImageButton messageIcon;
    private ImageButton toolbarTitle;
    private ImageButton profileIcon;

    private Fragment linkFragment;
    private Fragment chatsFragment;
    private Fragment configurationsFragment;



    public enum EnterAnimation {
        FROM_RIGHT,
        FROM_LEFT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        linkFragment = new LinkFragment();
        chatsFragment = new ChatsFragment();
        configurationsFragment = new ConfigurationsFragment();

        setContentView(R.layout.activity_main);
        messageIcon = (ImageButton) findViewById(R.id.icon_chats);
        profileIcon = (ImageButton) findViewById(R.id.icon_profile);
        toolbarTitle = (ImageButton) findViewById(R.id.toolbar_title);

        messageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(chatsFragment, EnterAnimation.FROM_RIGHT, CHATS_FRAGMENT);
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(configurationsFragment, EnterAnimation.FROM_LEFT,CONFIGURATIONS_FRAGMENT);
            }
        });

        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment chatsFragment = getSupportFragmentManager().findFragmentByTag(CHATS_FRAGMENT);
                if (chatsFragment != null && chatsFragment.isVisible()) {
                    changeFragment(linkFragment, EnterAnimation.FROM_LEFT, LINK_FRAGMENT);
                }
                changeFragment(linkFragment, EnterAnimation.FROM_RIGHT, LINK_FRAGMENT);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentFragment, linkFragment, LINK_FRAGMENT)
                    .commit();
        }
/*
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment chatsFragment = new ChatsFragment();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.contentFragment, chatsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        /*
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.logoutButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(currentUser!=null) {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                    startActivity(intent);
                }
            }
        });*/

    }

    public void changeFragment(Fragment fragment, EnterAnimation animation, String FragmentTag){
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (animation == EnterAnimation.FROM_LEFT){
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        }else {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        }
        transaction.replace(R.id.contentFragment, fragment, FragmentTag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
