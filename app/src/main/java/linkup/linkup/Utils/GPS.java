package linkup.linkup.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import linkup.linkup.model.UserLocation;

/**
 * Created by andres on 9/14/17.
 */
//https://stackoverflow.com/questions/5783611/android-best-way-to-implement-locationlistener-across-multiple-activities?rq=1

public class GPS {

    private static final long MIN_TIME_INTERVAL_MILISECONDS = 600000;//10 Minutes
    private static final float MIN_DISTANCE_METERS = 1000 ;
    private IGPSActivity main;

    // Helper for GPS-Position
    private LocationListener mlocListener;
    private LocationManager mlocManager;

    private boolean isRunning;

    public GPS(IGPSActivity main) {
        this.main = main;

        // GPS Position
        mlocManager = (LocationManager) ((Activity) this.main).getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        resumeGPS();
        // GPS Position END
        this.isRunning = true;
    }

    public void stopGPS() {
        if(isRunning) {
            mlocManager.removeUpdates(mlocListener);
            this.isRunning = false;
        }
    }


    public void resumeGPS() {
        if (ActivityCompat.checkSelfPermission((Context) this.main, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            this.main.displayGPSSettingsDialog();
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL_MILISECONDS, MIN_DISTANCE_METERS, mlocListener);
        this.isRunning = true;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public class MyLocationListener implements LocationListener {

        private final String TAG = MyLocationListener.class.getSimpleName();

        @Override
        public void onLocationChanged(Location loc) {
            GPS.this.main.locationChanged(loc.getLongitude(), loc.getLatitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            GPS.this.main.displayGPSSettingsDialog();
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

}
