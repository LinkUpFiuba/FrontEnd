package linkup.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

import static java.security.AccessController.getContext;

/**
 * Created by german on 9/9/2017.
 */

public class SplashAcitivity extends BaseActivity {
    private static final String TAG = "SplashAcitivity";
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        if ( mAuth.getCurrentUser() != null){
            Log.d(TAG,"Entra");
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
