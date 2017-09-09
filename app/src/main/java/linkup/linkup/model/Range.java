package linkup.linkup.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by andres on 9/5/17.
 */
@IgnoreExtraProperties

public class Range implements Serializable {
    public int minAge;
    public int maxAge;
    private final int defaultInterval=5;

    public Range (){

    }
    public Range(int age){
        minAge=age-defaultInterval;
        if(minAge<18){
            minAge=18;
        }
        maxAge=age+defaultInterval;
    }
}
