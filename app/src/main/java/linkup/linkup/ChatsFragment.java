package linkup.linkup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import linkup.linkup.adapter.ChatRoomsAdapter;
import linkup.linkup.helper.SimpleDividerItemDecoration;
import linkup.linkup.model.ChatRoom;
import linkup.linkup.model.Match;
import linkup.linkup.model.Message;
import linkup.linkup.model.SerializableUser;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;


public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";
    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    private Context context;
    private RelativeLayout backgroundNoChats;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        init(view);
        fetchChatRooms();

        return view;
    }

    private void init(View view) {


        context = getActivity().getApplicationContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.conversations_recycler_view);

        chatRoomArrayList = new ArrayList<>();

        mAdapter = new ChatRoomsAdapter(context, chatRoomArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(context, recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChatRoom chatRoom = chatRoomArrayList.get(position);
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("chatRead", chatRoom.isRead());
                intent.putExtra("notifyBloqued", chatRoom.isNotifyBloquedByOtherUser());
                intent.putExtra("BloquedType", chatRoom.getTypeBlock());
                intent.putExtra("user", chatRoom.getUser());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        backgroundNoChats = (RelativeLayout) view.findViewById(R.id.contentNoChatRooms);

    }

    private void updateRowLastMessage(String chatRoomId, String lastMessage) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(lastMessage);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void updateBloquedInfo(String chatRoomId,  String type) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setNotifyBloquedByOtherUser(true);
                cr.setTypeBlock(type);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void updateRowUnreadChatRoom(String chatRoomId, boolean read) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setRead(read);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(0, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void removeUserFromView(String chatRoomId) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                chatRoomArrayList.remove(index);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void updateUnreadCount(String chatRoomId, int unreadCount) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setUnreadCount(unreadCount);
                if(cr.getUnreadCount() > 0){
                    chatRoomArrayList.remove(index);
                    chatRoomArrayList.add(0, cr);
                }else {
                    chatRoomArrayList.remove(index);
                    chatRoomArrayList.add(index, cr);
                }
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void fetchChatRooms() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        User user = SingletonUser.getUser();

        databaseReference.child("matches").child(user.getSerializableUser().getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.getChildrenCount();
                Log.d(TAG, Long.toString(count));

                if (count == 0 && chatRoomArrayList.size() == 0) {
                    backgroundNoChats.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Match match = snapshot.getValue(Match.class);
                        if(match.getBlock() != null){
                            if(match.getBlock().isRead()){
                                count = count -1;
                            }
                        }
                    }
                    if (count == 0){
                        backgroundNoChats.setVisibility(View.VISIBLE);
                    }else {
                        backgroundNoChats.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReference.child("matches").child(user.getSerializableUser().getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String key = dataSnapshot.getKey();
                Match match = dataSnapshot.getValue(Match.class);
                //Log.d(TAG, key);
                if(match.getBlock() == null){
                    fetchUserInformation(key, false);
                }else {
                    if((!match.getBlock().getBy().equals(SingletonUser.getUser().getSerializableUser().getId())) && match.getBlock().isRead() == false){
                        fetchUserInformation(key, true);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                String key = dataSnapshot.getKey();
                Match match = dataSnapshot.getValue(Match.class);
                if(match.getBlock() != null) {
                    //Si no soy yo el que bloquea y todavia no lo lei
                    if((!match.getBlock().getBy().equals(SingletonUser.getUser().getSerializableUser().getId())) && match.getBlock().isRead() == false){

                        updateBloquedInfo(key,match.getBlock().getType());
                    }else{
                        removeUserFromView(key);
                    }
                }
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

    private void fetchUserInformation(String key, final boolean bloqued) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if (dataSnapshot.exists()) {
                    user = (User) dataSnapshot.getValue(User.class);
                    SerializableUser userSerial = user.getSerializableUser();
                    Log.d(TAG, userSerial.getName() + userSerial.getAge() + userSerial.getPhotoURL());

                    ChatRoom cr = new ChatRoom();
                    cr.setId(userSerial.getId());
                    cr.setUser(userSerial);
                    cr.setLastMessage("");
                    cr.setUnreadCount(0);
                    cr.setNotifyBloquedByOtherUser(bloqued);
                    chatRoomArrayList.add(cr);

                    mAdapter.notifyDataSetChanged();
                    backgroundNoChats.setVisibility(View.INVISIBLE);

                    fetchLastMessageData(cr);
                    fetchUnreadCount(cr);
                    fetchChatRoomRead(cr);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void fetchLastMessageData(final ChatRoom cr) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        Log.d(TAG, SingletonUser.getUser().getSerializableUser().getId() + " " + cr.getUser().getId());

        ref.child("messages").child(SingletonUser.getUser().getSerializableUser().getId()).child(cr.getUser().getId()).orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Message map = dataSnapshot.getValue(Message.class);
                Log.d(TAG, map.getMessage());

                updateRowLastMessage(cr.getId(), map.getMessage());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchChatRoomRead(final ChatRoom cr) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("matches").child(SingletonUser.getUser().getSerializableUser().getId()).child(cr.getUser().getId()).child("read").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    boolean data = (boolean) dataSnapshot.getValue();
                    Log.d(TAG, Boolean.toString(data));
                    updateRowUnreadChatRoom(cr.getId(), data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchUnreadCount(final ChatRoom cr) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("messages").child(SingletonUser.getUser().getSerializableUser().getId()).child(cr.getUser().getId()).orderByChild("read").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int size = (int) dataSnapshot.getChildrenCount();
                Log.d(TAG, String.valueOf(size));
                updateUnreadCount(cr.getId(), size);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
