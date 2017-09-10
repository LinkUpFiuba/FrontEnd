package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

public class SplashAcitivity extends BaseActivity {
    private static final String TAG = "SplashAcitivity";
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG,"antes");

        mAuth = FirebaseAuth.getInstance();
        if ( mAuth.getCurrentUser() != null){

            for (UserInfo user: mAuth.getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com")) {

                    super.createOrGetUser(mAuth.getCurrentUser());
                }
            }
        }else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }
}
