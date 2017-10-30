package linkup.linkup.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import fiuba.cardstack.SwipeDeck;
import linkup.linkup.MainActivity;
import linkup.linkup.ProfileActivity;
import linkup.linkup.R;
import linkup.linkup.Utils.LikeObserver;
import linkup.linkup.model.Advertisement;
import linkup.linkup.model.CardSwipeContent;
import linkup.linkup.model.Like;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;


public class SwipeDeckAdapter extends BaseAdapter {

    public static final int REQUEST_CODE_PROFILE = 100;
    private final SwipeDeck cardStack;
    private List<CardSwipeContent> data;
    private int AD = 100;
    private int CANDIDATE = 200;
    private Context context;

    public SwipeDeckAdapter(List<CardSwipeContent> data, Context context, SwipeDeck cardStack) {
        this.data = data;
        this.context = context;
        this.cardStack = cardStack;
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;

        }
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        final CardSwipeContent currrentCardSwipeContent = (CardSwipeContent) getItem(position);
        if(currrentCardSwipeContent.getType() == CardSwipeContent.AD){
            return AD;
        }
        return CANDIDATE;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            if (getItemViewType(position) == CANDIDATE) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_card, parent, false);
                inflateCandidateView(position, v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_card, parent, false);
                inflateAdView(position, v);
            }
        }


        return v;
    }

    private void inflateAdView(int position, View v) {
        if (getItem(position) != null) {
            Advertisement ad = ((CardSwipeContent) getItem(position)).getAd();
            final ImageView imageView = (ImageView) v.findViewById(R.id.ad_image);
            Picasso.with(context).load(ad.getUrlImage()).fit().centerCrop().into(imageView);

            Button buttonOk = (Button)v.findViewById(R.id.btnOkAd);
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardStack.swipeTopCardRight(180);
                }
            });
        }
    }

    private void inflateCandidateView(int position, View v) {
        if (getItem(position) != null) {
            final User currrentUser = ((CardSwipeContent) getItem(position)).getUser();

            final ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);
            Picasso.with(context).load(currrentUser.photoUrl).fit().centerCrop().into(imageView);

            TextView textView = (TextView) v.findViewById(R.id.cardNameAndAge);
            textView.setText(currrentUser.name + ", " + currrentUser.age);

            TextView textView2 = (TextView) v.findViewById(R.id.cardWork);
            textView2.setText(currrentUser.work);

            TextView textView3 = (TextView) v.findViewById(R.id.cardEducation);
            textView3.setText(currrentUser.education);

            TextView cardDistance = (TextView) v.findViewById(R.id.cardDistance);
            cardDistance.setText(String.valueOf(Math.round(currrentUser.distance)) + " km");

            TextView cardcommonLikes = (TextView) v.findViewById(R.id.cardcommonLikes);
            cardcommonLikes.setText(String.valueOf(currrentUser.commonLikes.size()));


            FloatingActionButton fabLike = (FloatingActionButton) v.findViewById(R.id.fabLike);
            FloatingActionButton fabDontLike = (FloatingActionButton) v.findViewById(R.id.fabDontLike);
            FloatingActionButton fabSuperLike = (FloatingActionButton) v.findViewById(R.id.fabSuperLike);
            fabLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: postear el like
                    LikeObserver.setLike();
                    cardStack.swipeTopCardRight(180);
                }
            });
            fabSuperLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: postear el superlike
                    User user= SingletonUser.getUser();
                    if(user.hasAvailableSuperLinks()){
                        LikeObserver.setSuperLike();
                        cardStack.swipeTopCardRight(180);
                    }else{
                        if(user.linkUpPlus){
                            Toast.makeText(v.getContext(), "No tienes más Superlinks, a partir de mañana tendrás más. ", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(v.getContext(), "No tienes más Superlinks, si quieres disponer de más puedes conseguir LinkUp Plus.", Toast.LENGTH_LONG).show();

                        }
                    }

                }
            });
            fabDontLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO: postear el nolike
                    cardStack.swipeTopCardLeft(180);
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hwardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));
                    Intent i = new Intent(v.getContext(), ProfileActivity.class);
                    i.putExtra("user", currrentUser.getSerializableUser());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((MainActivity) v.getContext(),
                                    imageView,
                                    ViewCompat.getTransitionName(imageView));
                    ((MainActivity) v.getContext()).startActivityForResult(i, REQUEST_CODE_PROFILE, options.toBundle());
                }
            });
        }
    }

}
