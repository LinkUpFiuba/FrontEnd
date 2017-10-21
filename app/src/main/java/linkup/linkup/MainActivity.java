package linkup.linkup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Iterator;

import linkup.linkup.Utils.GPS;
import linkup.linkup.Utils.IGPSActivity;
import linkup.linkup.adapter.SwipeDeckAdapter;
import linkup.linkup.model.Message;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;
import linkup.linkup.model.UserLocation;

import static android.R.attr.data;

public class MainActivity extends BaseActivity implements IGPSActivity {

    public static final String CHATS_FRAGMENT = "CHATS_FRAGMENT";
    public static final String LINK_FRAGMENT = "LINK_FRAGMENT";
    private static final String TAG = "MainActivity";
    private static final String PREFS_FILE_NAME = "PREFS_FILE_NAME";

    private Toolbar toolbar;
    private AlertDialog gpsAlertDialog;
    private AlertDialog permissionsAlertDialog;
    private LinkFragment linkFragment;
    private ChatsFragment chatsFragment;

    private DrawerLayout drawerLayout;
    private GPS gps;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    private ImageButton icChatsToolbar;

    public enum EnterAnimation {
        FROM_RIGHT,
        FROM_LEFT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = SingletonUser.getUser();
        if (user == null) {
            startLoginActivity();
            return;
        } else {


            gps = new GPS(this);
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

                if(!user.photoUrl.isEmpty()) {
                    ImageView imageView1 = (ImageView) headerview.findViewById(R.id.circle_image);
                    Picasso.with(this).load(user.photoUrl).fit().centerCrop().into(imageView1);
                }
                TextView menu_name_age = (TextView) headerview.findViewById(R.id.menu_name_age);
                menu_name_age.setText(user.name + ", " + user.age);
                TextView menu_work = (TextView) headerview.findViewById(R.id.menu_work);
                menu_work.setText(user.work);
                TextView menu_education = (TextView) headerview.findViewById(R.id.menu_education);
                menu_education.setText(user.education);

                navigationDrawerHeaderContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startMyProfileActivity();
                    }
                });

                ImageButton icGameToolbar = (ImageButton) findViewById(R.id.game_icon_toolbar);
                icGameToolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeFragment(linkFragment, EnterAnimation.FROM_LEFT, LINK_FRAGMENT);
                        toolbar.setTitle(getResources().getString(R.string.icon_game));
                    }
                });

                icChatsToolbar = (ImageButton) findViewById(R.id.chats_icon_toolbar);
                icChatsToolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        icChatsToolbar.setImageResource(R.drawable.ic_chat_bubble_white_24dp);
                        changeFragment(chatsFragment, EnterAnimation.FROM_RIGHT, CHATS_FRAGMENT);
                        toolbar.setTitle(getResources().getString(R.string.icon_chats));
                    }
                });
                iconChatsToolbarSetAtention();
            }

            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.contentFragment, linkFragment, LINK_FRAGMENT)
                        .commit();
                toolbar.setTitle(getResources().getString(R.string.icon_game));
            }

            registerOnNewActivityNotify();
        }
    }


    private void selectItem(String menuItem) {
        if (menuItem == getResources().getString(R.string.nav_item_link)) {
            changeFragment(linkFragment, EnterAnimation.FROM_LEFT, LINK_FRAGMENT);
            toolbar.setTitle(getResources().getString(R.string.nav_item_link));

        } else if (menuItem == getResources().getString(R.string.nav_item_chats)) {
            changeFragment(chatsFragment, EnterAnimation.FROM_RIGHT, CHATS_FRAGMENT);
            toolbar.setTitle(getResources().getString(R.string.nav_item_chats));

        } else if (menuItem == getResources().getString(R.string.nav_item_edit_profile)) {
            startMyProfileActivity();

        } else if (menuItem == getResources().getString(R.string.nav_item_edit_account)) {
            starEditAccountSettingsActivity();

        } else if (menuItem == getResources().getString(R.string.nav_item_edit_logout)) {
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


    public void changeFragment(Fragment fragment, EnterAnimation animation, String FragmentTag) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (animation == EnterAnimation.FROM_LEFT) {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        }
        transaction.replace(R.id.contentFragment, fragment, FragmentTag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        linkFragment.showEmptyCardStack();
        linkFragment.startAnimation();
        if (!gps.isRunning()) gps.resumeGPS();
    }

    @Override
    public void onStop() {
        gps.stopGPS();
        super.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startMyProfileActivity() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }

    public void starEditAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountConfigProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment linkFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("LINK_FRAGMENT");
        Fragment chatsFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("CHATS_FRAGMENT");

        if (linkFragment != null && linkFragment.isVisible()) {
            toolbar.setTitle(getResources().getString(R.string.icon_game));
        }
        if (chatsFragment != null && chatsFragment.isVisible()) {
            toolbar.setTitle(getResources().getString(R.string.icon_chats));
        }
    }

    @Override
    public void locationChanged(double longitude, double latitude) {
        User user = SingletonUser.getUser();
        if (user != null) {
            UserLocation userLocation = user.location;
            userLocation.longitude = longitude;
            userLocation.latitude = latitude;
            updateUser(user, false);
        }

    }

    private void createAndShowPermissionsAlertDialog() {
        if (permissionsAlertDialog != null && permissionsAlertDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Para poder usar esta aplicacion debes darle permiso para usar tu localización.");
        final Activity thisActivity = this;
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
            }
        });
        builder.setCancelable(false);
        permissionsAlertDialog = builder.create();
        permissionsAlertDialog.show();

    }

    @Override
    public void displayGPSSettingsDialog() {
        //https://medium.com/@muthuraj57/handling-runtime-permissions-in-android-d9de2e18d18f
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            createAndShowPermissionsAlertDialog();

        } else {
            if (isFirstTimeAskingPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                firstTimeAskingPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                createAndShowPermissionsAlertDialog();

                //Permission disable by device policy or user denied permanently. Show proper error message
            }
        }


    }

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    @Override
    public void displayGPSEnabledDialog() {
        if (gpsAlertDialog != null && gpsAlertDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final Context context = getBaseContext();
        builder.setMessage("Para poder usar esta aplicacion debe tener habilitada la localización.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(myIntent);
            }
        });
        builder.setCancelable(false);
        gpsAlertDialog = builder.create();
        gpsAlertDialog.show();
        if (this.linkFragment != null && this.linkFragment.isTaskRunning()) {
            this.linkFragment.stopTask();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            this.gps.resumeGPS();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SwipeDeckAdapter.REQUEST_CODE_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                LinkFragment linkFragment = (LinkFragment) getSupportFragmentManager().findFragmentByTag("LINK_FRAGMENT");
                String result = data.getStringExtra("result");
                Log.d(TAG, result);
                if (result.equals(ProfileActivity.LIKE)) {
                    Log.d(TAG, "entra");
                    linkFragment.swipeRight();
                } else if (result.equals(ProfileActivity.DONT_LIKE)) {
                    linkFragment.swipeLeft();
                } else if (result.equals(ProfileActivity.SUPER_LIKE)) {
                    linkFragment.swipeSuperLike();
                }

            }
        }

    }

    public void registerOnNewActivityNotify() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.child("matches").child(SingletonUser.getUser().getSerializableUser().getId()).orderByChild("read").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int size = (int) dataSnapshot.getChildrenCount();
                Log.d(TAG, String.valueOf(size));
                if (size > 0) {
                    iconChatsToolbarSetAtention();
                } else {
                    iconChatsToolbarUnSetAtention();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.child("messages").child(SingletonUser.getUser().getSerializableUser().getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message msg = snapshot.getValue(Message.class);
                    if (!msg.isRead()) {
                        iconChatsToolbarSetAtention();
                        return;
                    }
                }
                iconChatsToolbarUnSetAtention();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message msg = snapshot.getValue(Message.class);
                    if (!msg.isRead()) {
                        iconChatsToolbarSetAtention();
                        return;
                    }
                }
                iconChatsToolbarUnSetAtention();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void iconChatsToolbarSetAtention() {
        icChatsToolbar.setImageResource(R.drawable.ic_chat_bubble_black_24dp);
    }

    public void iconChatsToolbarUnSetAtention() {
        icChatsToolbar.setImageResource(R.drawable.ic_chat_bubble_white_24dp);
    }
}
