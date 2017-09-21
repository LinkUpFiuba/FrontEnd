package linkup.linkup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import linkup.linkup.adapter.ChatRoomsAdapter;
import linkup.linkup.helper.SimpleDividerItemDecoration;
import linkup.linkup.model.ChatRoom;
import linkup.linkup.model.Message;
import linkup.linkup.model.SerializableUser;

import static android.R.id.message;


public class ChatsFragment extends Fragment {
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
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("user", chatRoom.getUser());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    /**
     * Actualiza la cantidad de mensajes no leidos y ultimo mensaje de un chatRoom
     */
    private void updateRow(String chatRoomId, Message message) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message.getMessage());
                cr.setUnreadCount(cr.getUnreadCount() + 1);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Ahora esta hardCodeado pero luego debe hacer el fetch
     * fetching the chat rooms by making http call
     */
    private void fetchChatRooms() {

        SerializableUser user = new SerializableUser("1","1","German","","", "", "", "", "", "","","");

        ChatRoom cr = new ChatRoom();
        cr.setId("1");
        cr.setUser(user);
        cr.setLastMessage("Hola camila!");
        cr.setUnreadCount(0);
        cr.setTimestamp("1460090232");

        chatRoomArrayList.add(cr);

        ChatRoom cr2 = new ChatRoom();
        cr2.setId("2");
        cr2.setUser(user);
        cr2.setLastMessage("Hola camila!");
        cr2.setUnreadCount(1);
        cr2.setTimestamp("1460090232");

        chatRoomArrayList.add(cr2);

        ChatRoom cr3 = new ChatRoom();
        cr3.setId("3");
        cr3.setUser(user);
        cr3.setLastMessage("chau loco");
        cr3.setUnreadCount(2);
        cr3.setTimestamp("1457413053");

        chatRoomArrayList.add(cr3);
    }
}
