package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import linkup.linkup.adapter.ChatRoomThreadAdapter;
import linkup.linkup.model.Message;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

/**
 * Created by german on 9/13/2017.
 */

public class ChatRoomActivity extends BaseActivity {
    private EditText inputMessage;
    private ImageButton btnSend;
    private SerializableUser otherUser;
    private RecyclerView recyclerView;
    private ArrayList<Message> messageArrayList;
    private ChatRoomThreadAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fabSendMessage);
        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.fabSendMessage);
        TextView titleChat = (TextView) findViewById(R.id.title_chat);
        recyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);

        messageArrayList = new ArrayList<>();

        Intent intent = getIntent();
        String chatRoomId = intent.getStringExtra("chat_room_id");
        otherUser = intent.getParcelableExtra("user");
        titleChat.setText(otherUser.getName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String selfUserId = SingletonUser.getUser().getSerializableUser().getId();
        Log.d("ChatRoom",selfUserId + " " + otherUser.getFbId());

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    /**
     * Post de un nuevo mensaje
     * */
    private void sendMessage() {
        final String messageToSend = this.inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageToSend)) {
            return;
        }
        this.inputMessage.setText("");

        Message message = new Message();
        message.setId("1");
        message.setMessage(messageToSend);
        message.setCreatedAt("");
        //message.setUser(SingletonUser.getUser().getSerializableUser());
        message.setUser(otherUser);

        messageArrayList.add(message);

        mAdapter.notifyDataSetChanged();
        if (mAdapter.getItemCount() > 1) {
            // scrolling to bottom of the recycler view
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
        }
    }
}
