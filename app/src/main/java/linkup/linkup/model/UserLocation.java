package linkup.linkup.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andres on 9/14/17.
 */
@IgnoreExtraProperties

public class UserLocation {

    public double longitude,latitude;
    public UserLocation(){
        longitude = 0;
        latitude = 0;
    }
}
