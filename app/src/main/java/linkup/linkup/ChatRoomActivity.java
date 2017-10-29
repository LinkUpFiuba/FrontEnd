package linkup.linkup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
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
import linkup.linkup.Utils.DataBase;
import linkup.linkup.adapter.ChatRoomThreadAdapter;
import linkup.linkup.adapter.LoadEarlierMessages;
import linkup.linkup.model.Block;
import linkup.linkup.model.ChatRoom;
import linkup.linkup.model.Match;
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
    private boolean chatRoomIsRead;
    private RelativeLayout backgroundNoMessages;

    private boolean acitivityInForegraund = false;

    @Override
    protected void onResume() {
        super.onResume();
        acitivityInForegraund = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        acitivityInForegraund = false;
    }

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
        otherUser = intent.getParcelableExtra("user");
        chatRoomIsRead = intent.getBooleanExtra("chatRead", false);
        boolean notifyBloqued = intent.getBooleanExtra("notifyBloqued", false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selfUserId = SingletonUser.getUser().getSerializableUser().getId();

        titleChat.setText(otherUser.getName());
        if(notifyBloqued){
            final String type = intent.getStringExtra("BloquedType");
            youHasBennBloqued(type);
        }else {
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

                    startActivity(i, options.toBundle());
                }
            });

            mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(mAdapter);

            backgroundNoMessages = (RelativeLayout) findViewById(R.id.contentNoMessages);

            oldestKey = "";
            ValueEventListener messagesCountValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    count = dataSnapshot.getChildrenCount();
                    Log.d(TAG, Long.toString(count));

                    backgroundNoMessages.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                    if (count == 0 && messageArrayList.size() == 0) {
                        backgroundNoMessages.setVisibility(View.VISIBLE);
                        mAdapter.setLoadEarlierMsgs(false);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }

                    if (count > 0 && messageArrayList.size() == 0) {
                        fetchHistory();
                    } else {
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

            databaseReference1.child("matches").child(selfUserId).child(otherUser.getId()).child("block").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Block block = (Block) dataSnapshot.getValue(Block.class);
                        if(block.isRead() == false){
                            youHasBennBloqued(block.getType());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void youHasBennBloqued(final String type) {
        if(this.isFinishing()){
           return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this).setTitle("Bloqueado").setMessage("Has sido bloqueado por el usuario");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                DataBase.deleteBloquedMatch(otherUser.getId(), SingletonUser.getUser().getSerializableUser().getId());
                onBackPressed();
                finish();
            }

        }).show();
    }

    private void postReadMessage(String key, Message map) {
        if (!map.isRead() && acitivityInForegraund) {
            Log.d(TAG, "posteo mensaje leido");
            databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).child(key).child("read").setValue(true);
        }
    }

    private void postReadChatRoom() {
        if (!chatRoomIsRead) {
            databaseReference1.child("matches").child(selfUserId).child(otherUser.getId()).child("read").setValue(true);
        }
    }

    private void updateRowLiked(String messageKeyId, boolean liked) {
        for (Message msg : messageArrayList) {
            if (msg.getKey().equals(messageKeyId)) {
                int index = messageArrayList.indexOf(msg);
                msg.setLiked(liked);
                messageArrayList.remove(index);
                messageArrayList.add(index, msg);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void fetchHistory() {
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).orderByKey().limitToLast(PAGINATION).addChildEventListener(new ChildEventListener() {
            int index = 0;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Message map = dataSnapshot.getValue(Message.class);
                String key = dataSnapshot.getKey();
                map.setKey(key);

                postReadChatRoom();
                postReadMessage(key, map);

                if (index == 0) {
                    oldestKey = key;
                }
                index = index + 1;
                addUIMessage(map);
                if (messageArrayList.size() == count) {
                    mAdapter.setLoadEarlierMsgs(false);
                } else {
                    mAdapter.setLoadEarlierMsgs(true);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Message map = dataSnapshot.getValue(Message.class);
                String key = dataSnapshot.getKey();
                map.setKey(key);
                Log.d(TAG, "Cambio en el mensaje: " + map.isLiked() );
                updateRowLiked(key,map.isLiked());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Post de un nuevo mensaje
     */
    private void sendMessage() {
        final String messageToSend = this.inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageToSend)) {
            return;
        }
        this.inputMessage.setText("");

        Message message = new Message();
        message.setMessage(messageToSend);
        message.setUserId(selfUserId);

        DatabaseReference ref = databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).push();
        String key = ref.getKey();
        Log.d(TAG,key);
        ref.setValue(message);

        databaseReference1.child("messages").child(otherUser.getId()).child(selfUserId).child(key).setValue(message);
    }

    private void addUIMessage(Message message) {
        backgroundNoMessages.setVisibility(View.INVISIBLE);

        if (messageArrayList != null) {
            messageArrayList.add(message);
        }

        if (mAdapter != null && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                // scrolling to bottom of the recycler view
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }

        }
    }

    private void loadMoreHistory() {

        final String lastOldestKey = oldestKey;
        Log.d(TAG, oldestKey);
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).orderByKey().endAt(oldestKey).limitToLast(PAGINATION + 1).addChildEventListener(new ChildEventListener() {
            int index = 0;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String key = dataSnapshot.getKey();

                Log.d(TAG, oldestKey + " " + key);

                if (!lastOldestKey.equals(key)) {
                    Message map = dataSnapshot.getValue(Message.class);
                    map.setKey(key);
                    postReadMessage(key,map);

                    if (index == 0) {
                        oldestKey = key;
                    }
                    if (messageArrayList != null) messageArrayList.add(index, map);
                    if (mAdapter != null && mAdapter != null) {
                        backgroundNoMessages.setVisibility(View.INVISIBLE);
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, index);
                        }
                        if (messageArrayList.size() == count) {
                            mAdapter.setLoadEarlierMsgs(false);
                        } else {
                            mAdapter.setLoadEarlierMsgs(true);
                        }
                    }
                    index = index + 1;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Message map = dataSnapshot.getValue(Message.class);
                String key = dataSnapshot.getKey();
                map.setKey(key);
                updateRowLiked(key,map.isLiked());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onLoadMore() {
        loadMoreHistory();
    }

    @Override
    public void postLikeMessage(Message message) {
        Log.d(TAG, "posteo mensaje like");
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).child(message.getKey()).child("liked").setValue(true);
        databaseReference1.child("messages").child(otherUser.getId()).child(selfUserId).child(message.getKey()).child("liked").setValue(true);
    }

    @Override
    public void postUnLikeMessage(Message message) {
        Log.d(TAG, "posteo mensaje unlike");
        databaseReference1.child("messages").child(selfUserId).child(otherUser.getId()).child(message.getKey()).child("liked").setValue(false);
        databaseReference1.child("messages").child(otherUser.getId()).child(selfUserId).child(message.getKey()).child("liked").setValue(false);
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