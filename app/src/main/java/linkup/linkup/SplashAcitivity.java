package linkup.linkup;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

public class SplashAcitivity extends BaseActivity {
    private static final String TAG = "SplashAcitivity";
    private static final int PERMISSION_LOCATION_REQUEST_CODE =1 ;
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

                    super.getIDToken();
                }
            }
        }else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }
}
