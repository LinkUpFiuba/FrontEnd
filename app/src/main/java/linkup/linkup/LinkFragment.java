package linkup.linkup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import connections.GetUsersAsyncTask;
import connections.ViewWithCards;
import fiuba.cardstack.SwipeDeck;
import linkup.linkup.Utils.DataBase;
import linkup.linkup.adapter.SwipeDeckAdapter;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.Unlink;
import linkup.linkup.model.User;


public class LinkFragment extends Fragment implements ViewWithCards {
    private static final String TAG = "Connect_fragment";
    RippleAnimation rippleBackground1;
    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;
    private GetUsersAsyncTask task;
    private RelativeLayout backgroundNoMoreCandidates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void swipeRight() {
        this.cardStack.swipeTopCardRight(180);
    }

    public void swipeLeft() {
        this.cardStack.swipeTopCardLeft(180);
    }

    public void swipeSuperLike() {
        superLike();
        this.cardStack.swipeTopCardRight(180);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_link, container, false);
        init(view);
        startAnimation();
        return view;
    }

    private void init(View view) {
        rippleBackground1 = (RippleAnimation) view.findViewById(R.id.content);
        rippleBackground1.setVisibility(View.VISIBLE);

        cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
        cardStack.setHardwareAccelerationEnabled(true);

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);

        backgroundNoMoreCandidates = (RelativeLayout) view.findViewById(R.id.contentNoMoreCandidates);
        Button retry = (Button) view.findViewById(R.id.btn_retry_candidates);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });
    }

    public void startAnimation() {
        backgroundNoMoreCandidates.setVisibility(View.INVISIBLE);
        //if it's not running
        if (!rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.setVisibility(View.VISIBLE);
            rippleBackground1.startRippleAnimation();
        }
        Log.d(TAG, "token" + SingletonUser.getToken());

        task = new GetUsersAsyncTask(this);
        task.execute();
    }

    private void stopAnimation() {
        if (rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.stopRippleAnimation();
            rippleBackground1.setVisibility(View.GONE);
        }
    }

    public void stopTask(){
        task.cancel(true);
    }
    public boolean isTaskRunning(){
        return (task.getStatus()== AsyncTask.Status.RUNNING);

    }
    public void showEmptyCardStack() {
        showCards(null, false);
    }

    @Override
    public void showCards(List<User> users, boolean showToasts) {

        if (showToasts) {
            if (users == null) {
                backgroundNoMoreCandidates.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Hubo un error en la conexion, vuelve a linkear mas tarde.",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (users.size() == 0) {
                    backgroundNoMoreCandidates.setVisibility(View.VISIBLE);
                }else {
                    backgroundNoMoreCandidates.setVisibility(View.INVISIBLE);
                }
            }
        }
        adapter = new SwipeDeckAdapter(users, getActivity(), cardStack);
        cardStack.setAdapter(adapter);
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                dontLike(adapter,position);
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);

            }

            @Override
            public void cardSwipedRight(int position) {
                like(adapter,position);
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);

            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                //TODO aca va de nuevo la petición al http server, ojo que si supera el max hay que mostrar otra pantalla
            }

            @Override
            public void cardActionDown() {
                Log.i(TAG, "cardActionDown");
            }

            @Override
            public void cardActionUp() {
                Log.i(TAG, "cardActionUp");
            }

        });
        stopAnimation();
    }

    public void like(SwipeDeckAdapter adapter, int position) {
        //TODO: postear el like
        Log.i("MainActivity", "Like");
        User user = (User) adapter.getItem(position);
        User myUser= SingletonUser.getUser();
        DataBase.saveLink(myUser.link(user),this);
    }

    public void superLike() {
        //TODO: postear el superlike
        Log.i("MainActivity", "SuperLike");

    }

    public void dontLike(SwipeDeckAdapter adapter, int position) {
        //TODO: postear el nolike
        Log.i("MainActivity", "DontLike");
        User user = (User) adapter.getItem(position);
        User myUser= SingletonUser.getUser();
        Unlink unlink= myUser.unlink(user);
        DataBase.saveUnlink(unlink,this);

    }

    public void ifCardsDepletedStartAnimation() {
        if(cardStack.getChildCount()<=0){
            startAnimation();
        }
    }
}
/*
 ValueEventListener videoValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String date = dataSnapshot.getKey();
                String temp = date;

               long count =  dataSnapshot.getChildrenCount();
                String value = dataSnapshot.getValue().toString();
                temp = value;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(tag, "database error");
            }
        };
//        videosQuery.addChildEventListener(videosChildEventListener);
        videosQuery.addValueEventListener(videoValueEventListener);

    }
 */