package linkup.linkup.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import linkup.linkup.R;
import linkup.linkup.model.Message;

/**
 * Esta clase adapter alinea a la derecha y a la izquierda inflando 2 layouts diferentes
 */
public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100;

    private ArrayList<Message> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    public ChatRoomThreadAdapter( ArrayList<Message> messageArrayList, String userId) {
        this.messageArrayList = messageArrayList;
        this.userId = userId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == SELF) {
            Log.d("ChatRoom","yo");

            // mi mensaje
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // mensaje de otros
            Log.d("ChatRoom","otros");

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getUserId().equals(userId)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        ((ViewHolder) holder).message.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

}
