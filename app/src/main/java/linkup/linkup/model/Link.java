package linkup.linkup.model;

/**
 * Created by andres on 9/20/17.
 */

public class Link {
    public static final String SUPERLINK = "superlink";
    public static final String NORMAL = "normal";
    public String linkingUser;
    public String linkedUser;
    public String type;
    public Link(){

    }
    public static  Link createLink(User linkingUser,User linkedUser){
        Link link=new Link();
        link.linkingUser =linkingUser.Uid;
        link.linkedUser =linkedUser.Uid;
        link.type = NORMAL;
        return link;
    }
    public static Link createSuperLink(User linkingUser,User linkedUser){
        Link link=new Link();
        link.linkingUser =linkingUser.Uid;
        link.linkedUser =linkedUser.Uid;
        link.type = SUPERLINK;
        return link;
    }
}
