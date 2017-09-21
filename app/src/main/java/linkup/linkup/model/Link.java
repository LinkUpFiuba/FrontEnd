package linkup.linkup.model;

/**
 * Created by andres on 9/20/17.
 */

public class Link {
    public String LinkingUid;
    public String LinkedUid;
    public Link(){

    }
    public Link(User linkingUser,User linkedUser){
        LinkingUid=linkingUser.Uid;
        LinkedUid=linkedUser.Uid;
    }
}
