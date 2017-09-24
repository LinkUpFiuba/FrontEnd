package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import linkup.linkup.adapter.ChatRoomThreadAdapter;
import linkup.linkup.adapter.LoadEarlierMessages;
import linkup.linkup.model.ChatRoom;
import linkup.linkup.model.Message;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;

import static linkup.linkup.adapter.SwipeDeckAdapter.REQUEST_CODE_PROFILE;


public class ChatRoomActivity extends BaseActivity implements LoadEarlierMessages {
    private static final String TAG = "ChatRoomActivity";
    public static final int PAGINATION = 15;
    private EditText inputMessage;
    private FloatingActionButton btnSend;
    private SerializableUser otherUser;
    private RecyclerView recyclerView;
    private ArrayList<Message> messageArrayList;
    private ChatRoomThreadAdapter mAdapter;
    private String selfUserId;
    private DatabaseReference databaseReference1;
    private String oldestKey = "";

    private long count = 0;
    private boolean chatRoomIsRead;
    private RelativeLayout backgroundNoMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference1 = database.getReference();

        setContentView(R.layout.activity_chat_room);

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (FloatingActionButton) findViewById(R.id.fabSendMessage);
        TextView titleChat = (TextView) findViewById(R.id.title_chat);
        recyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);

        final CircleImageView profileImageView = (CircleImageView) findViewById(R.id.profile_img_toolbar);

        messageArrayList = new ArrayList<>();

        Intent intent = getIntent();
        chatRoomIsRead = intent.getBooleanExtra("chatRead",false);

        otherUser = intent.getParcelableExtra("user");
        titleChat.setText(otherUser.getName());

        Picasso.with(this).load(otherUser.getPhotoURL()).fit().centerCrop().into(profileImageView);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatRoomActivity.this, ViewProfileActivity.class);
                i.putExtra("user", otherUser);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ChatRoomActivity.this,
                                profileImageView,
                                ViewCompat.getTransitionName(profileImageView));

                startActivity(i,options.toBundle());
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selfUserId = SingletonUser.getUser().getSerializableUser().getId();
        //Log.d("ChatRoom",selfUserId + " " + otherUser.getFbId());

        mAdapter = new ChatRoomThreadAdapter(this,messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        backgroundNoMessages = (RelativeLayout) findViewById(R.id.contentNoMessages);

        oldestKey = "";
        ValueEventListener messagesCountValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count =  dataSnapshot.getChildrenCount();
                Log.d(TAG, Long.toString(count));

                backgroundNoMessages.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                if(count == 0 && messageArrayList.size() == 0 ) {
                    backgroundNoMessages.setVisibility(View.VISIBLE);
                    mAdapter.setLoadEarlierMsgs(false);
                    recyclerView.setVisibility(View.INVISIBLE);
                }

                if(count > 0 && messageArrayList.size() == 0 ){
                    fetchHistory();
                }else{
                    mAdapter.setLoadEarlierMsgs(false);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).addValueEventListener(messagesCountValueEventListener);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                //loadMoreHistory();
            }
        });
    }
    private void postReadMessage(String key){
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).child(key).child("read").setValue(true);
    }

    private void postReadChatRoom(){
        if(!chatRoomIsRead){
            databaseReference1.child("matches").child(selfUserId).child(otherUser.getId()).child("read").setValue(true);
        }
    }

    private void fetchHistory() {
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).orderByKey().limitToLast(PAGINATION).addChildEventListener(new ChildEventListener() {
            int index = 0;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Message map = dataSnapshot.getValue(Message.class);
                String key = dataSnapshot.getKey();
                postReadChatRoom();
                postReadMessage(key);

                if(index == 0){
                    oldestKey = key;
                }
                index = index + 1;
                addUIMessage(map);
                if( messageArrayList.size() == count ){
                    mAdapter.setLoadEarlierMsgs(false);
                }else {
                    mAdapter.setLoadEarlierMsgs(true);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
        message.setMessage(messageToSend);
        message.setUserId(selfUserId);

        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).push().setValue(message);
        databaseReference1.child("messages").child(otherUser.getId()).child(selfUserId).push().setValue(message);
    }

    private void addUIMessage(Message message){
        backgroundNoMessages.setVisibility(View.INVISIBLE);

        if(messageArrayList!= null){
            messageArrayList.add(message);
        }

        if (mAdapter != null && mAdapter!= null ){
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                // scrolling to bottom of the recycler view
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }

        }
    }

    private void loadMoreHistory(){

        final String lastOldestKey = oldestKey;
        Log.d(TAG, oldestKey);
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).orderByKey().endAt(oldestKey).limitToLast(PAGINATION + 1).addChildEventListener(new ChildEventListener() {
            int index = 0;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String key = dataSnapshot.getKey();
                postReadMessage(key);

                Log.d(TAG, oldestKey + " " + key );

                if (!lastOldestKey.equals(key)) {
                    Message map = dataSnapshot.getValue(Message.class);

                    if(index == 0){
                        oldestKey = key;
                    }
                    if(messageArrayList!= null) messageArrayList.add(index, map);
                    if (mAdapter != null && mAdapter!= null ) {
                        backgroundNoMessages.setVisibility(View.INVISIBLE);
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, index);
                        }
                        if( messageArrayList.size() == count ){
                            mAdapter.setLoadEarlierMsgs(false);
                        }else {
                            mAdapter.setLoadEarlierMsgs(true);
                        }
                    }
                    index = index + 1;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onLoadMore() {
        loadMoreHistory();
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
/*

 */