package linkup.linkup.model;

/**
 * Created by german on 10/15/2017.
 */

public class Advertisement {
    String urlImage;

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Advertisement() {

    }

    public Advertisement(String urlImage) {

        this.urlImage = urlImage;
    }
}
