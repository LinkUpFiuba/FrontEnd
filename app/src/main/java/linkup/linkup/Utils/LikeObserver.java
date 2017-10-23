package linkup.linkup.Utils;

/**
 * Created by andres on 10/23/17.
 */

public class LikeObserver {
    private static boolean isSuperLike;
    private static final LikeObserver ourInstance = new LikeObserver();

    public static LikeObserver getInstance() {
        return ourInstance;
    }

    private LikeObserver() {
    }

    public static void setSuperLike(){
        isSuperLike=true;
    }

    public static void setLike(){
        isSuperLike=false;
    }

    public static boolean isSuperLike(){
        return isSuperLike;
    }

    public static boolean isLike(){
        return (!isSuperLike);
    }
}
