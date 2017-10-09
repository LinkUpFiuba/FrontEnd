package linkup.linkup.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import linkup.linkup.R;
import linkup.linkup.model.ChatRoom;


public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> {
    private static final int UNREAD =100;
    private static final int READ = 200;
    private Context mContext;
    private ArrayList<ChatRoom> chatRoomArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, count;
        public CircleImageView profileImageView;

        public ViewHolder(View view) {
            super(view);
            profileImageView = (CircleImageView) view.findViewById(R.id.profile_img);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            count = (TextView) view.findViewById(R.id.count);
        }
    }


    public ChatRoomsAdapter(Context mContext, ArrayList<ChatRoom> chatRoomArrayList) {
        this.mContext = mContext;
        this.chatRoomArrayList = chatRoomArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == UNREAD) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_rooms_list_row_new, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_rooms_list_row, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomArrayList.get(position);
        holder.name.setText(chatRoom.getUser().getName());

        if(chatRoom.isNotifyBloquedByOtherUser() == false){
            Picasso.with(mContext).load(chatRoom.getUser().getPhotoURL()).fit().centerCrop().into(holder.profileImageView);
            holder.message.setText(chatRoom.getLastMessage());

            //Ahow
            if (chatRoom.getUnreadCount() > 0) {
                holder.count.setText(String.valueOf(chatRoom.getUnreadCount()));
                holder.count.setVisibility(View.VISIBLE);
            } else {
                holder.count.setVisibility(View.GONE);
            }
        }else {
            Picasso.with(mContext).load(R.drawable.womn).fit().centerCrop().into(holder.profileImageView);

            holder.message.setText("Has sido bloqueado por el usuario");
            //holder.count.setVisibility(View.GONE);
            holder.count.setText("!");
            holder.count.setVisibility(View.VISIBLE);
        }


        //holder.timestamp.setText(DateHelper.convertTimeToDateString(mContext, chatRoom.getTimestampLong(), true,true,true,false,false,true,false));
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ChatRoomsAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ChatRoomsAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public int getItemViewType(int position) {
            ChatRoom chatRoom = chatRoomArrayList.get(position);
            if (chatRoom.isRead() == false) {
                return UNREAD;
            }else {
                return READ;
            }

    }

}
