package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

/**
 * Created by german on 9/17/2017.
 */

public class NewMatchActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_match);
        Button btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatActivity();
            }
        });
        Button btnLater = (Button) findViewById(R.id.btnLater);
        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageView profile_img_left = (ImageView) findViewById(R.id.profile_img_left);
        User user = SingletonUser.getUser();
        Picasso.with(this).load(user.photoUrl).fit().centerCrop().into(profile_img_left);
        String photo = getIntent().getStringExtra("Photo");

        ImageView profile_img_right = (ImageView) findViewById(R.id.profile_img_right);
        Picasso.with(this).load(photo).fit().centerCrop().into(profile_img_right);
        String name = getIntent().getStringExtra("Name");
        TextView subtitle= (TextView) findViewById(R.id.subtitle);
        subtitle.setText(name+" "+getString(R.string.activity_new_match_subtitle));


    }
    private void startChatActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
