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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fiuba.cardstack.SwipeDeck;
import linkup.linkup.MainActivity;
import linkup.linkup.ProfileActivity;
import linkup.linkup.R;
import linkup.linkup.model.User;


public class SwipeDeckAdapter extends BaseAdapter {

    public static final int REQUEST_CODE_PROFILE = 100;
    private final SwipeDeck cardStack;
    private List<User> data;
    private Context context;

    public SwipeDeckAdapter(List<User> data, Context context, SwipeDeck cardStack) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_card, parent, false);
        }
        if (getItem(position) != null) {
            final User currrentUser = (User) getItem(position);

            final ImageView imageView = (ImageView) v.findViewById(R.id.offer_image);
            Picasso.with(context).load(currrentUser.photoUrl).fit().centerCrop().into(imageView);

            TextView textView = (TextView) v.findViewById(R.id.cardNameAndAge);
            textView.setText(currrentUser.name + ", " + currrentUser.age);

            TextView textView2 = (TextView) v.findViewById(R.id.cardWork);
            textView2.setText(currrentUser.work);

            TextView textView3 = (TextView) v.findViewById(R.id.cardEducation);
            textView3.setText(currrentUser.education);

            FloatingActionButton fabLike = (FloatingActionButton) v.findViewById(R.id.fabLike);
            FloatingActionButton fabDontLike = (FloatingActionButton) v.findViewById(R.id.fabDontLike);
            FloatingActionButton fabSuperLike = (FloatingActionButton) v.findViewById(R.id.fabSuperLike);
            fabLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: postear el like
                    cardStack.swipeTopCardRight(180);
                }
            });
            fabSuperLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: postear el superlike
                    cardStack.swipeTopCardRight(180);
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
        ;

        return v;
    }

}
