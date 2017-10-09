package linkup.linkup;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import linkup.linkup.model.SingletonUser;
import linkup.linkup.model.User;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String distance;
    private UiSettings uiSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        distance=getIntent().getStringExtra("distance");

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        User user= SingletonUser.getUser();
        LatLng userPosition = new LatLng(user.location.latitude, user.location.longitude);

        mMap.addMarker(new MarkerOptions().position(userPosition));
        Circle circle=mMap.addCircle(new CircleOptions().center(userPosition).radius(Integer.valueOf(distance)*1000).fillColor(Color.argb(50, 0, 50, 240)).strokeColor(Color.argb(50, 0, 50, 240)));

        int zoom=getZoomLevel(circle);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition,zoom));
        uiSettings = mMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(true);


    }
    private int getZoomLevel(Circle circle) {

            double radius = circle.getRadius();
            double scale = radius / 500;
            int zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
            return zoomLevel;

    }
}
