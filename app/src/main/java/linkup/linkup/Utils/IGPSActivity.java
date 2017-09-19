package linkup.linkup.Utils;

import android.content.Context;

/**
 * Created by andres on 9/14/17.
 */
//https://stackoverflow.com/questions/5783611/android-best-way-to-implement-locationlistener-across-multiple-activities?rq=1

public interface IGPSActivity  {
    public void locationChanged(double longitude, double latitude);
    public void displayGPSSettingsDialog();
    public void displayGPSEnabledDialog();

}
