package ldp.locations;

import org.zkoss.gmaps.LatLng;



/**
 * Created by web on 05.11.2016.
 */
public class ContainerLocation   {

    private LatLng location;
    private int agp;

    public ContainerLocation(LatLng location, int agp) {
        this.location = location;
        this.agp = agp;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getAgpString() {


        String title = "";
        if (agp > 0) {
            title += "Sorten: ";
            if ((agp & 4) == 4) {
                title += " Altkleider";
            }
            if ((agp & 2) == 2) {
                title += " Glas";
            }
            if ((agp & 1) == 1) {
                title += " Papier";
            }
        }
        return title;


    }

    public String getAgpImage() {
        String image;

        switch (agp) {
            case 10:
                image = "./mipmap-hdpi/glas.png";
                break;
            case 11:
                image = "./mipmap-hdpi/papier_glas.png";
                break;
            case 100:
                image = "./mipmap-hdpi/kleidung.png";
                break;
            case 110:
                image = "./mipmap-hdpi/glas_kleidung.png";
                break;
            case 111:
                image = "./mipmap-hdpi/papier_glas_kleidung.png";
                break;
            default:
                image = "./mipmap-hdpi/ic_launcher.png";
                break;
        }


        return image;


    }




}
