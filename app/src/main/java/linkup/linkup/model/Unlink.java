package linkup.linkup.model;

/**
 * Created by andres on 9/20/17.
 */

public class Unlink {
    public String unlinkingUser;
    public String unlinkedUser;
    public Unlink(){

    }
    public Unlink(User unlinkingUser, User unlinkedUser){
        this.unlinkingUser =unlinkingUser.Uid;
        this.unlinkedUser =unlinkedUser.Uid;
    }
}
