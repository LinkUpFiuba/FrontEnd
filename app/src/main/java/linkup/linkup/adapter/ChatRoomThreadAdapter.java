package linkup.linkup.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
    private int MORE = 200;
    private int OTHER = 300;


    private ArrayList<Message> messageArrayList;
    private final LoadEarlierMessages mLoadEarlierMessages;

    private boolean isLoadEarlierMsgs = true;

    public void setLoadEarlierMsgs(boolean loadEarlierMsgs) {
        isLoadEarlierMsgs = loadEarlierMsgs;
    }
    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout btnLoadMore;

        public LoadMoreViewHolder(View view) {
            super(view);
            btnLoadMore = (RelativeLayout) itemView.findViewById(R.id.containerLoadMore);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    public ChatRoomThreadAdapter(LoadEarlierMessages context, ArrayList<Message> messageArrayList, String userId) {
        this.messageArrayList = messageArrayList;
        this.userId = userId;
        this.mLoadEarlierMessages = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if(viewType == MORE) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_load_more, parent, false);
            return new LoadMoreViewHolder(itemView);
        }else{
            if (viewType == SELF) {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item_self, parent, false);
            } else {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item_other, parent, false);
            }
        }
        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return MORE;
        }else {
            Message message = messageArrayList.get(position - 1);
            if (message.getUserId().equals(userId)) {
                //Log.d("ChatRoom","yo" + message.getMessage());
                return SELF;
            }else {
                //Log.d("ChatRoom","otros" + message.getMessage());
                return OTHER;
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        if(type== MORE) {
            if (isLoadEarlierMsgs) {
                ((LoadMoreViewHolder) holder).btnLoadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mLoadEarlierMessages != null) {
                            mLoadEarlierMessages.onLoadMore();
                        }
                    }
                });
            } else {
                ((LoadMoreViewHolder) holder).btnLoadMore.setVisibility(View.GONE);
            }
        }else if(type== SELF)  {
            Message message = messageArrayList.get(position -1);

            ((ViewHolder) holder).message.setText(message.getMessage());
        }else {
            Message message = messageArrayList.get(position -1);

            ((ViewHolder) holder).message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size() + 1;
    }

}
