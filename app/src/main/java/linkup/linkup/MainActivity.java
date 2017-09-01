package linkup.linkup;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        setContentView(R.layout.activity_main);
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.logoutButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(currentUser!=null) {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                    startActivity(intent);
                }
            }
        });
    }


}
