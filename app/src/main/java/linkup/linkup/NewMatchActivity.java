package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import linkup.linkup.model.ChatRoom;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;


public class NewMatchActivity extends BaseActivity {
    private SerializableUser otherUser;

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

        otherUser = (SerializableUser) getIntent().getParcelableExtra("user");

        ImageView profile_img_right = (ImageView) findViewById(R.id.profile_img_right);
        Picasso.with(this).load(otherUser.getPhotoURL()).fit().centerCrop().into(profile_img_right);

        TextView subtitle= (TextView) findViewById(R.id.subtitle);
        subtitle.setText(otherUser.getName() +" "+getString(R.string.activity_new_match_subtitle));

    }
    private void startChatActivity(){
        ChatRoom cr = new ChatRoom();
        cr.setId(otherUser.getId());
        cr.setUser(otherUser);
        cr.setLastMessage("");
        cr.setUnreadCount(0);

        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("chatRead", cr.isRead());
        intent.putExtra("user", cr.getUser());
        startActivity(intent);
        finish();
    }
}
