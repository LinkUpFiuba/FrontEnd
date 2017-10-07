package linkup.linkup.adapter;

import linkup.linkup.model.Message;

public interface LoadEarlierMessages {
    void onLoadMore();
    void postLikeMessage(Message message);
    void postUnLikeMessage(Message message);

}
