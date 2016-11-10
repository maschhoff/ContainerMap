/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ldp.maps;

/**
 *
 * @author web
 */
import java.io.File;
import java.util.ArrayList;
import ldp.locations.ContainerLocation;
import ldp.locations.Locations;
import org.apache.commons.io.FileUtils;
import org.zkoss.gmaps.Gmarker;
import org.zkoss.gmaps.event.MapMouseEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.gmaps.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;

public class GoogleMapsController extends org.zkoss.zul.Window implements org.zkoss.zk.ui.ext.AfterCompose {
//public class GoogleMapsController extends SelectorComposer<Component>{

    private Gmaps gmaps;

    // Java-Mehtod that will be called by JavaScript
    public void locationReceieved(org.zkoss.zk.ui.event.Event e) {
        System.err.println("locationReceieved()");

        Object s = e.getData();

        try {
            //  org.zkoss.zul.Messagebox.show("Successfully received Data: \n\n" + s);   
            String location = (String) s;
            String[] array = location.split(";");
            LatLng ltln = new LatLng(Double.valueOf(array[0]), Double.valueOf(array[1]));
            System.err.println("change to " + ltln.getLatitude() + " " + ltln.getLongitude());
            gmaps.setCenter(ltln);
        } catch (Exception ex) {
            System.err.println("ERROR");
        }
    }

    public void afterCompose() {
        System.err.println("afterCompose()");

        gmaps = (Gmaps) getFellow("gmaps");

        gmaps.setZoom(14);
        gmaps.setCenter(new LatLng(51.4935, 7.3106));

        gmaps.setEnableGoogleBar(true);
        gmaps.setShowZoomCtrl(true);

        Locations.initLocations();
        ArrayList<ContainerLocation> array = Locations.containerLocations;

        System.err.println("cl array: " + array.size());

        for (ContainerLocation cl : array) {
            Gmarker mark = new Gmarker("<b>Container</b><br/>" + cl.getAgpString(), cl.getLocation());
            mark.setIconImage(cl.getAgpImage());
            mark.setIconHeight(50);
            mark.setIconWidth(50);
            gmaps.appendChild(mark);
        }

    }

    
}
