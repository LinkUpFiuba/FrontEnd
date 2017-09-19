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

import static android.R.id.message;
import static linkup.linkup.R.id.count;


public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";
    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    private Context context;

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
                intent.putExtra("user", chatRoom.getUser());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

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

    private void updateRowUnreadChatRoom(String chatRoomId, boolean read) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setRead(read);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
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
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void fetchChatRooms() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        User user = SingletonUser.getUser();
        databaseReference.child("matches").child(user.getSerializableUser().getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String key = dataSnapshot.getKey();
                Match match = dataSnapshot.getValue(Match.class);
                Log.d(TAG, key);
                fetchUserInformation(key,match.isRead());
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

    private void fetchUserInformation(String key, boolean read) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                if(dataSnapshot.exists()) {
                    user = (User) dataSnapshot.getValue(User.class);
                    SerializableUser userSerial = user.getSerializableUser();
                    Log.d(TAG,userSerial.getName() + userSerial.getAge() + userSerial.getPhotoURL());

                    ChatRoom cr = new ChatRoom();
                    cr.setId(userSerial.getId());
                    cr.setUser(userSerial);
                    cr.setLastMessage("");
                    cr.setUnreadCount(0);
                    chatRoomArrayList.add(cr);

                    mAdapter.notifyDataSetChanged();
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

    public void fetchLastMessageData(final ChatRoom cr){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        Log.d(TAG, SingletonUser.getUser().getSerializableUser().getId() + " " + cr.getUser().getId());

        ref.child("messages").child(SingletonUser.getUser().getSerializableUser().getId()).child(cr.getUser().getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Message map = dataSnapshot.getValue(Message.class);
                Log.d(TAG,map.getMessage());

                updateRowLastMessage(cr.getId(),map.getMessage());
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
                boolean data = (boolean) dataSnapshot.getValue();
                Log.d(TAG,Boolean.toString(data));
                updateRowUnreadChatRoom(cr.getId(),data);
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
                Log.d(TAG,String.valueOf(size));
                updateUnreadCount(cr.getId(),size);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

        /**
         * Ahora esta hardCodeado pero luego debe hacer el fetch
         * fetching the chat rooms by making http call
         */
    private void fetchChatRoomsExample() {

        SerializableUser user = new SerializableUser("1","1","German","","", "", "", "", "", "");

        ChatRoom cr = new ChatRoom();
        cr.setId("1");
        cr.setUser(user);
        cr.setLastMessage("Hola camila!");
        cr.setUnreadCount(0);

        chatRoomArrayList.add(cr);

        ChatRoom cr2 = new ChatRoom();
        cr2.setId("2");
        cr2.setUser(user);
        cr2.setLastMessage("Hola camila!");
        cr2.setUnreadCount(1);

        chatRoomArrayList.add(cr2);

        ChatRoom cr3 = new ChatRoom();
        cr3.setId("3");
        cr3.setUser(user);
        cr3.setLastMessage("chau loco");
        cr3.setUnreadCount(2);

        chatRoomArrayList.add(cr3);
    }
}
