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
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import linkup.linkup.adapter.ChatRoomThreadAdapter;
import linkup.linkup.adapter.LoadEarlierMessages;
import linkup.linkup.model.Message;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;


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

        messageArrayList = new ArrayList<>();

        Intent intent = getIntent();
        //String chatRoomId = intent.getStringExtra("chat_room_id");
        otherUser = intent.getParcelableExtra("user");
        titleChat.setText(otherUser.getName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selfUserId = SingletonUser.getUser().getSerializableUser().getId();
        Log.d("ChatRoom",selfUserId + " " + otherUser.getFbId());

        mAdapter = new ChatRoomThreadAdapter(this,messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        oldestKey = "";
        ValueEventListener messagesCountValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count =  dataSnapshot.getChildrenCount();
                Log.d(TAG, Long.toString(count));

                if(count > 0 && messageArrayList.size() == 0 ){
                    fetchHistory();
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

    private void fetchHistory() {
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).orderByKey().limitToLast(PAGINATION).addChildEventListener(new ChildEventListener() {
            int index = 0;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Message map = dataSnapshot.getValue(Message.class);
                String key = dataSnapshot.getKey();
                if(index == 0){
                    oldestKey = key;
                }
                index = index + 1;
                addUIMessage(map);
                if( messageArrayList.size() == count ){
                    mAdapter.setLoadEarlierMsgs(false);
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
        if(messageArrayList!= null) messageArrayList.add(message);

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
                Log.d(TAG, oldestKey + " " + key );

                if (!lastOldestKey.equals(key)) {
                    Message map = dataSnapshot.getValue(Message.class);

                    if(index == 0){
                        oldestKey = key;
                    }
                    if(messageArrayList!= null) messageArrayList.add(index, map);
                    if (mAdapter != null && mAdapter!= null ) {
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, index);
                        }
                        if( messageArrayList.size() == count ){
                            mAdapter.setLoadEarlierMsgs(false);
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
}
/*

 */