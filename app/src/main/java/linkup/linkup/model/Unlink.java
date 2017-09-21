package linkup.linkup.model;

/**
 * Created by andres on 9/20/17.
 */

public class Unlink {
    public String unlinkingUid;
    public String unlinkedUid;
    public Unlink(){

    }
    public Unlink(User unlinkingUser, User unlinkedUser){
        unlinkingUid =unlinkingUser.Uid;
        unlinkedUid =unlinkedUser.Uid;
    }
}
