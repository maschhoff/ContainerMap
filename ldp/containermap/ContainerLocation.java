package ldp.containermap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by web on 05.11.2016.
 */
public class ContainerLocation implements ClusterItem {

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

    public int getAgpImage() {
        int image;

        switch (agp) {
            case 10:
                image = R.mipmap.glas;
                break;
            case 11:
                image = R.mipmap.papier_glas;
                break;
            case 100:
                image = R.mipmap.kleidung;
                break;
            case 110:
                image = R.mipmap.glas_kleidung;
                break;
            case 111:
                image = R.mipmap.papier_glas_kleidung;
                break;
            default:
                image = R.mipmap.ic_launcher;
                break;
        }


        return image;


    }


    @Override
    public LatLng getPosition() {
        return location;
    }


}
