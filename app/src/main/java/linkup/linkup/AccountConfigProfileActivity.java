package linkup.linkup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class AccountConfigProfileActivity extends BaseActivity {

    public static final int MIN_VALUE = 18;
    public static final int MAX_VALUE = 100;
    private User user;
    private static String TAG="AccountConfig";
    boolean changed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditAccount);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getResources().getString(R.string.nav_item_edit_account));
        }

        user=SingletonUser.getUser();
        setSwitchInvisibleMode();
        setSearchMenSwitch();
        setSearchWomenSwitch();
        setNotificationsSwitch();
        setSearchFriendSwitch();
        setLogOffButton();
        setDeleteButton();
        setLinkUpPlusButton();

        setSeekBarAgeRange();
        setSeekBarDistanceRange();


    }

    private void setLinkUpPlusButton() {
        final Button btnObtenerLinkUpPlus = (Button)findViewById(R.id.btnObtenerLinkUpPlus);
        btnObtenerLinkUpPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LinkUpPlusActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setDeleteButton() {
        final Button logOffButton = (Button)findViewById(R.id.btnDeleteAccount);
        logOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(AccountConfigProfileActivity.this, R.style.AppThemeDialog);
                builder.setMessage("¿Desea usted eliminar esta cuenta?");
                builder.setCancelable(false);

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                            deleteAccount();


                    }

                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                    }

                });
                builder.show();

            }

        });
    }


    private void setSeekBarAgeRange() {
        // get seekbar from view
        final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) findViewById(R.id.rangeSeekbarAge);
        User user=SingletonUser.getUser();
        rangeSeekbar.setMinStartValue(user.range.minAge);
        rangeSeekbar.setMaxStartValue(user.range.maxAge);
        rangeSeekbar.setMinValue(MIN_VALUE);
        rangeSeekbar.setMaxValue(MAX_VALUE);
        rangeSeekbar.apply();
        // get min and max text view
        final TextView ageRangeText = (TextView) findViewById(R.id.ageRangeText);
        ageRangeText.setText(String.valueOf(user.range.minAge)+"-"+String.valueOf(user.range.maxAge));

// set final value listener
        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ageRangeText.setText(String.valueOf(minValue.intValue())+"-"+String.valueOf(maxValue.intValue()));

            }


        });
        // set final value listener
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                ageRangeText.setText(String.valueOf(minValue.intValue())+"-"+String.valueOf(maxValue.intValue()));
                User user=SingletonUser.getUser();
                changed=true;

                user.range.minAge= (int) minValue.intValue();
                user.range.maxAge= (int) maxValue.intValue();
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });
    }
    private void setSeekBarDistanceRange() {
        final CrystalSeekbar distanceSeekbar = (CrystalSeekbar) findViewById(R.id.seekbarDistance);
        User user=SingletonUser.getUser();
        distanceSeekbar.setMinStartValue(user.maxDistance);
        distanceSeekbar.apply();
        // get min and max text view
        final TextView distanceRangeText = (TextView) findViewById(R.id.distanceRangeText);
        distanceRangeText.setText(String.valueOf(user.maxDistance));

        // set  value listener
        distanceSeekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                distanceRangeText.setText(String.valueOf(value));
            }
        });
        // set final value listener
        distanceSeekbar.setOnSeekbarFinalValueListener(new OnSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number value) {
                distanceRangeText.setText(String.valueOf(value));
                User user=SingletonUser.getUser();
                changed=true;

                user.maxDistance= (int) value.intValue();

            }


        });
    }

    private void setLogOffButton() {
        final Button logOffButton = (Button)findViewById(R.id.btnLogOut2);
        logOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

    }

    private void setSwitchInvisibleMode(){
        Switch switchInvisibleMode = (Switch)findViewById(R.id.switchInvisibleMode);
        updateSwitchInvisibleMode();
        switchInvisibleMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changed=true;
                user.invisibleMode=!isChecked;
            }

        });
    }
    private void updateSwitchInvisibleMode(){
        Switch switchInvisibleMode = (Switch)findViewById(R.id.switchInvisibleMode);

        switchInvisibleMode.setChecked(!user.invisibleMode);
    }
    private void setSearchMenSwitch(){
        Switch switchSearchesMen = (Switch)findViewById(R.id.switchSearchesMen);

        updateSearchMenSwitch();
        switchSearchesMen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changed=true;
                user.interests.setSearchesMen(isChecked);
                updateInterestsSwitches();
            }
        });
    }
    private void updateSearchMenSwitch(){
        Switch switchSearchesMen = (Switch)findViewById(R.id.switchSearchesMen);

        switchSearchesMen.setChecked(user.interests.searchesMen());
    }
    private void setNotificationsSwitch(){
        Switch switchNotifications = (Switch)findViewById(R.id.switchNotifications);
        updateNotificationsSwitch();
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changed=true;
                user.getNotifications=isChecked;

            }
        });
    }
    private void updateNotificationsSwitch(){
        Switch switchNotifications = (Switch)findViewById(R.id.switchNotifications);

        switchNotifications.setChecked(user.getNotifications);
    }
    private void setSearchWomenSwitch(){
        Switch switchSearchesWomen = (Switch)findViewById(R.id.switchSearchesWomen);

        updateSearchWomenSwitch();
        switchSearchesWomen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changed=true;
                user.interests.setSearchesWomen(isChecked);
                updateInterestsSwitches();
            }
        });
    }
    private void updateSearchWomenSwitch(){
        Switch switchNotifications = (Switch)findViewById(R.id.switchSearchesWomen);

        switchNotifications.setChecked(user.interests.searchesWomen());
    }
    private void setSearchFriendSwitch(){
        Switch switchSearchesFriends = (Switch)findViewById(R.id.switchSearchesFriends);
        updateSearchFriendSwitch();
        switchSearchesFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changed=true;
                user.interests.setSearchesFriends(isChecked);
                updateInterestsSwitches();
            }
        });
    }
    private  void updateInterestsSwitches(){
        updateSearchWomenSwitch();
        updateSearchMenSwitch();
        updateSearchFriendSwitch();
    }
    private void updateSearchFriendSwitch(){
        Switch switchSearchesFriends = (Switch)findViewById(R.id.switchSearchesFriends);

        switchSearchesFriends.setChecked(user.interests.searchesFriends());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_account_menu, menu);
        return true;
    }
    private void updateIfChange(){
        if(changed){
            updateUser(SingletonUser.getUser(),true);

        }
        super.onBackPressed();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                updateIfChange();
            break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        updateIfChange();
    }


}
