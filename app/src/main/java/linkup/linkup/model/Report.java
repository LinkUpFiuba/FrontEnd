package linkup.linkup.model;

import com.google.firebase.database.Exclude;

/**
 * Created by andres on 9/28/17.
 */

public class Report {
    public String idReporting,message,state;
    @Exclude
    public String idReported;
    public Report(){}
    public Report(String idReporting,String idReported,String message){
        this.idReported=idReported;
        this.idReporting=idReporting;
        this.message=message;
        this.state="pending";
    }
}
