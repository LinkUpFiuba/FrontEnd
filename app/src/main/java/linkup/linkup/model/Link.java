package linkup.linkup.model;

/**
 * Created by andres on 9/20/17.
 */

public class Link {
    public String linkingUser;
    public String linkedUser;
    public Link(){

    }
    public Link(User linkingUser,User linkedUser){
        this.linkingUser =linkingUser.Uid;
        this.linkedUser =linkedUser.Uid;
    }
}
