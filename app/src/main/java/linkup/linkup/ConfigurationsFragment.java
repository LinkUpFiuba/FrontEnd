package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ConfigurationsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configurations, container, false);


        Button logOutButton = (Button) view.findViewById(R.id.LogOutButton);
                    logOutButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);

                                                startActivity(intent);
                                    }
                            }
         });
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

}
