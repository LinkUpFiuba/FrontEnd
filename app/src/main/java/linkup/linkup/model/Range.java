package linkup.linkup.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andres on 9/5/17.
 */
@IgnoreExtraProperties

public class Range {
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
