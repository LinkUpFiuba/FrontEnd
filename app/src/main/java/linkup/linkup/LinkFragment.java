package linkup.linkup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import connections.GetUsersAsyncTask;
import connections.ViewWithCards;
import fiuba.cardstack.SwipeDeck;
import linkup.linkup.adapter.SwipeDeckAdapter;
import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;


public class LinkFragment extends Fragment implements ViewWithCards {
    private static final String TAG = "Connect_fragment";
    RippleAnimation rippleBackground1;
    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
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
    }
    private void addCandidate(String candidate){
        //testData.add(candidate);
        adapter.notifyDataSetChanged();
    }
    public void startAnimation() {
        //if it's not running
        if (!rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.setVisibility(View.VISIBLE);
            rippleBackground1.startRippleAnimation();
        }
        Log.d(TAG, "token" + SingletonUser.getToken());

        GetUsersAsyncTask task = new GetUsersAsyncTask(this);
        task.execute();
    }

    private void stopAnimation() {
        if (rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.stopRippleAnimation();
            rippleBackground1.setVisibility(View.GONE);
        }

    }

    public void showEmptyCardStack(){
        showCards(null,false);
    }

    @Override
    public void showCards(List<User> users,boolean showToasts) {
         if(showToasts) {
             if (users == null) {
                 Toast.makeText(getActivity(), "Hubo un error en la conexion, vuelve a linkear mas tarde.",
                         Toast.LENGTH_SHORT).show();
             } else {
                 if (users.size() == 0) {
                     Toast.makeText(getActivity(), "No hay mas candidatos, intenta mas tarde.",
                             Toast.LENGTH_SHORT).show();
                 }
             }
         }
        adapter = new SwipeDeckAdapter(users, getActivity());
        cardStack.setAdapter(adapter);
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                startAnimation();
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
}
