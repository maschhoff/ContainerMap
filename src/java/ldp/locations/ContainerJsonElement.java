package ldp.locations;

/**
 * Created by web on 07.11.2016.
 */

public class ContainerJsonElement {

    String Lat, Lng, AGP, Ort;

    public ContainerJsonElement(String lat, String lng, String AGP, String ort) {
        Lat = lat;
        Lng = lng;
        this.AGP = AGP;
        Ort = ort;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getAGP() {
        return AGP;
    }

    public void setAGP(String AGP) {
        this.AGP = AGP;
    }

    public String getOrt() {
        return Ort;
    }

    public void setOrt(String ort) {
        Ort = ort;
    }
}
