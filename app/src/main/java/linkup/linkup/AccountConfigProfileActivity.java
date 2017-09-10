package linkup.linkup;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class AccountConfigProfileActivity extends BaseActivity {

    private User user;
    private static String TAG="AccountConfig";
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
        setSeekBarRange();

    }

    private void setSeekBarRange() {
        // get seekbar from view
        final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) findViewById(R.id.rangeSeekbar1);
        User user=SingletonUser.getUser();
        rangeSeekbar.setMinStartValue(user.range.minAge);
        rangeSeekbar.setMaxStartValue(user.range.maxAge);
        rangeSeekbar.setMinValue(18);
        rangeSeekbar.setMaxValue(69);

        // get min and max text view
        final TextView ageRangeText = (TextView) findViewById(R.id.ageRangeText);
        ageRangeText.setText(String.valueOf(user.range.minAge)+"-"+String.valueOf(user.range.maxAge));


        // set final value listener
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                ageRangeText.setText(String.valueOf(minValue.intValue())+"-"+String.valueOf(maxValue.intValue()));
                User user=SingletonUser.getUser();
                user.range.minAge= (int) minValue.intValue();
                user.range.maxAge= (int) maxValue.intValue();
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                updateUser(SingletonUser.getUser());
            break;
        }
        return super.onOptionsItemSelected(item);
    }


}
