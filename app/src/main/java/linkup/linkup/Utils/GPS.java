package linkup.linkup.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import linkup.linkup.model.UserLocation;

/**
 * Created by andres on 9/14/17.
 */
//https://stackoverflow.com/questions/5783611/android-best-way-to-implement-locationlistener-across-multiple-activities?rq=1

public class GPS {

    private static final long MIN_TIME_INTERVAL_MILISECONDS = 30000;//30 segundos
    private static final float MIN_DISTANCE_METERS = 50 ;
    private IGPSActivity main;

    // Helper for GPS-Position
    private LocationListener mlocListener;
    private LocationManager mlocManager;
    private Location currentLocation;
    private boolean isRunning;

    public GPS(IGPSActivity main) {
        this.main = main;

        // GPS Position
        mlocManager = (LocationManager) ((Activity) this.main).getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        if(checkForPermission()&&checkIfLocationIsEnabled()) {
            Location gpsProviderLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location networkProviderLocation=mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(isBetterLocation(networkProviderLocation,gpsProviderLocation)){
                currentLocation=networkProviderLocation;
            }else {
                currentLocation=gpsProviderLocation;
            }
            //this.main.locationChanged(currentLocation.getLongitude(),currentLocation.getLatitude());
            resumeGPS();
            // GPS Position END
            this.isRunning = true;
        }

    }

    public void stopGPS() {
        if(isRunning) {
            mlocManager.removeUpdates(mlocListener);
            this.isRunning = false;
        }
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    private boolean checkIfLocationIsEnabled(){

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
        if(!gps_enabled || !network_enabled) {
            // notify user
            this.main.displayGPSEnabledDialog();
            return false;
        }
        return true;
        }
    private boolean checkForPermission(){
        if (ActivityCompat.checkSelfPermission((Context) this.main, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            this.main.displayGPSSettingsDialog();
            return false;
        }return true;
    }
    public void resumeGPS() {
        if(!(checkForPermission()&&checkIfLocationIsEnabled())){
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL_MILISECONDS, MIN_DISTANCE_METERS, mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_INTERVAL_MILISECONDS, MIN_DISTANCE_METERS, mlocListener);

        this.isRunning = true;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public class MyLocationListener implements LocationListener {

        private final String TAG = MyLocationListener.class.getSimpleName();

        @Override
        public void onLocationChanged(Location loc) {
            if(isBetterLocation(loc,currentLocation)) {
                GPS.this.main.locationChanged(loc.getLongitude(), loc.getLatitude());
                currentLocation = loc;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(!GPS.this.checkForPermission()) {
                GPS.this.main.displayGPSSettingsDialog();
            }else {
                GPS.this.main.displayGPSEnabledDialog();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }
    //https://developer.android.com/guide/topics/location/strategies.html#BestEstimate
    private static final int MAX_TIME_OLDER_LOCATION_MILISECONDS = 1000 * 60 * 10;//10 minutos
    private static final int DISTANCE_TOLERABLE_FOR_ACCURACY = 100;//100 metros

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MAX_TIME_OLDER_LOCATION_MILISECONDS;
        boolean isSignificantlyOlder = timeDelta < -MAX_TIME_OLDER_LOCATION_MILISECONDS;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > DISTANCE_TOLERABLE_FOR_ACCURACY;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


}
