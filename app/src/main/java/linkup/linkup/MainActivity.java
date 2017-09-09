package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import connections.GetUsersAsyncTask;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity  {

    public static final String CHATS_FRAGMENT = "CHATS_FRAGMENT";
    public static final String LINK_FRAGMENT = "LINK_FRAGMENT";
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    private Toolbar toolbar;

    private Fragment linkFragment;
    private Fragment chatsFragment;

    private DrawerLayout drawerLayout;


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

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getResources().getString(R.string.icon_game));
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            View headerview = navigationView.getHeaderView(0);
            LinearLayout navigationDrawerHeaderContainer = (LinearLayout) headerview.findViewById(R.id.linearLayoutNavHeader);
            navigationDrawerHeaderContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMyProfileActivity();
                }
            });
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentFragment, linkFragment, LINK_FRAGMENT)
                    .commit();
            toolbar.setTitle(getResources().getString(R.string.icon_game));
        }



    }
    private void selectItem(String menuItem) {
        if(menuItem == getResources().getString(R.string.nav_item_link)) {
            changeFragment(linkFragment, EnterAnimation.FROM_LEFT, LINK_FRAGMENT);
            toolbar.setTitle(getResources().getString(R.string.nav_item_link));

        }else if (menuItem == getResources().getString(R.string.nav_item_chats)){
            changeFragment(chatsFragment, EnterAnimation.FROM_RIGHT, CHATS_FRAGMENT);
            toolbar.setTitle(getResources().getString(R.string.nav_item_chats));

        }else if (menuItem == getResources().getString(R.string.nav_item_edit_profile)) {
            startMyProfileActivity();

        }else if (menuItem == getResources().getString(R.string.nav_item_edit_account)) {
            starEditAccountSettingsActivity();

        }else if (menuItem == getResources().getString(R.string.nav_item_edit_logout)) {
            logOut();
        }

        drawerLayout.closeDrawers(); // Cerrar drawer

    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);
                        String title = menuItem.getTitle().toString();
                        selectItem(title);
                        return true;
                    }
                }
        );
    }
    public void logOut(){
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        User user= SingletonUser.get();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            View headerview = navigationView.getHeaderView(0);

            ImageView imageView1 = (ImageView) headerview.findViewById(R.id.circle_image);
            Picasso.with(this).load(user.photoUrl).fit().centerCrop().into(imageView1);

            TextView menu_name_age=(TextView) headerview.findViewById(R.id.menu_name_age);
            menu_name_age.setText(user.name+", "+user.age);
            TextView menu_work = (TextView) headerview.findViewById(R.id.menu_work);
            menu_work.setText(user.work);
            TextView menu_education = (TextView) headerview.findViewById(R.id.menu_education);
            menu_education.setText(user.education);
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
            case R.id.icon_game:
                changeFragment(linkFragment, EnterAnimation.FROM_LEFT, LINK_FRAGMENT);
                toolbar.setTitle(getResources().getString(R.string.icon_game));                return true;
            case R.id.icon_chats:
                changeFragment(chatsFragment, EnterAnimation.FROM_RIGHT, CHATS_FRAGMENT);
                toolbar.setTitle(getResources().getString(R.string.icon_chats));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void startMyProfileActivity(){
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }
    public void starEditAccountSettingsActivity(){
        Intent intent = new Intent(this, AccountConfigProfileActivity.class);
        startActivity(intent);
    }

}
