package ldp.containermap;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by web on 05.11.2016.
 */
public class Locations {


    public static ArrayList<ContainerLocation> containerLocations;


    public static void initLocations(Activity activity) {
        containerLocations = new ArrayList<>();
        containerLocations.addAll(Locations.getLatLngList(activity, R.raw.bochum));
        containerLocations.addAll(Locations.getLatLngList(activity, R.raw.essen));
        containerLocations.addAll(Locations.getLatLngList(activity, R.raw.dortmund));
        containerLocations.addAll(Locations.getLatLngList(activity, R.raw.duisburg));
        containerLocations.addAll(Locations.getLatLngList(activity, R.raw.unna));
    }


    public static ArrayList<ContainerLocation> getLatLngList(Activity activity, int city) {


        ArrayList<ContainerLocation> locations = new ArrayList<ContainerLocation>();

        try {

            String line = "";
            Scanner s = new Scanner(activity.getResources().openRawResource(city));
            while (s.hasNext()) {
                line += s.next();

            }
            Log.e("DEBUG", line);

            Gson gson = new Gson();
            List<ContainerJsonElement> array = gson.fromJson(line, new TypeToken<List<ContainerJsonElement>>() {
            }.getType());


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
