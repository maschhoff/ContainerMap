package ldp.locations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.zkoss.gmaps.LatLng;
import org.zkoss.zk.ui.Executions;

/**
 * Created by web on 05.11.2016.
 */
public class Locations {

    public static ArrayList<ContainerLocation> containerLocations;

    public static void initLocations() {
        System.err.println("initLocations...");
        containerLocations = new ArrayList<ContainerLocation>();
        containerLocations.addAll(Locations.getLatLngList("bochum"));
        containerLocations.addAll(Locations.getLatLngList("essen"));
        containerLocations.addAll(Locations.getLatLngList("dortmund"));
        containerLocations.addAll(Locations.getLatLngList("unna"));
        containerLocations.addAll(Locations.getLatLngList("duisburg"));

    }

    public static ArrayList<ContainerLocation> getLatLngList(String city) {
        ArrayList<ContainerLocation> locations = new ArrayList<ContainerLocation>();

        try {

            String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("./raw/" + city + ".json");
            System.err.println(path);
            String line = FileUtils.readFileToString(new File(path));

            Gson gson = new Gson();
            List<ContainerJsonElement> array = gson.fromJson(line, new TypeToken<List<ContainerJsonElement>>() {
            }.getType());

            System.err.println("gefunden: " + array.size());
            //Zur Liste Hinzufügen
            for (ContainerJsonElement cje : array) {
                ContainerLocation cl = new ContainerLocation(new LatLng(Double.valueOf(cje.getLat()), Double.valueOf(cje.getLng())), Integer.valueOf(cje.getAGP()));
                locations.add(cl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }

}
