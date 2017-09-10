package linkup.linkup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

import static linkup.linkup.R.id.radioButton;

public class FillMissingInformationActivity extends BaseActivity {

    private static String TAG="FillMissingnActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_gender);
        final RadioGroup genderRadioGroup = (RadioGroup) findViewById(R.id.genderRGroup);
        Button readyButton = (Button) findViewById(R.id.btnMissingInformationOk);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                Log.d(TAG,radioButton.getText().toString());
                String result = "";
                if (radioButton.getText().toString().equals(getResources().getString(R.string.missing_gender_male))){
                    result = "male";
                }else {
                    result = "female";
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logOut();
    }
}
