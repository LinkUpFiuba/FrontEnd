package linkup.linkup.model;

/**
 * Created by andres on 9/20/17.
 */

public class Link {
    public String linkingUid;
    public String linkedUid;
    public Link(){

    }
    public Link(User linkingUser,User linkedUser){
        linkingUid =linkingUser.Uid;
        linkedUid =linkedUser.Uid;
    }
}
