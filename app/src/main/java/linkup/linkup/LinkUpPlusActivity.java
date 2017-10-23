package linkup.linkup;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class LinkUpPlusActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_up_plus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLinkUpPlus);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getResources().getString(R.string.nav_item_linkup_plus));
        }
        final User user= SingletonUser.getUser();
        Button btnGetLinkUpPlus=(Button) findViewById(R.id.btn_getLinkUpPlus);
        Button btnOutLinkUpPlus=(Button) findViewById(R.id.btn_outLinkUpPlus);
        if(user.linkUpPlus) {
            btnGetLinkUpPlus.setVisibility(View.GONE);
            btnOutLinkUpPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.linkUpPlus=false;
                    updateUser(user,false);
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(LinkUpPlusActivity.this, R.style.AppThemeDialog);
                    builder.setTitle(getResources().getString(R.string.linkUpPlus_out_success_title));
                    builder.setMessage(getResources().getString(R.string.linkUpPlus_out_success_message));
                    builder.setIcon(R.drawable.ic_done_black_24dp);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();

                        }
                    });
                    if (!isFinishing()) {

                        builder.show();
                    }
                }
            });
        }else {
            btnOutLinkUpPlus.setVisibility(View.GONE);
            btnGetLinkUpPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.linkUpPlus=true;
                    updateUser(user,false);
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(LinkUpPlusActivity.this, R.style.AppThemeDialog);
                    builder.setTitle(getResources().getString(R.string.linkUpPlus_success_title));
                    builder.setMessage(getResources().getString(R.string.linkUpPlus_success_message));
                    builder.setIcon(R.drawable.ic_done_black_24dp);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();

                        }
                    });
                    if (!isFinishing()) {

                        builder.show();
                    }

                }
            });
        }
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

                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
