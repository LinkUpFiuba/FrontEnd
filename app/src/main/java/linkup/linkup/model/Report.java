package linkup.linkup.model;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andres on 9/28/17.
 */

public class Report {
    public String idReporting,message,state,timeStamp;
    @Exclude
    public String idReported;
    public Report(){}
    public Report(String idReporting,String idReported,String message){
        this.idReported=idReported;
        this.idReporting=idReporting;
        this.message=message;
        this.state="pending";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timeStamp=dateFormat.format(new Date());
    }
}
