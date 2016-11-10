/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ldp.maps;

import org.zkoss.gmaps.Gmarker;
import org.zkoss.gmaps.event.MapMouseEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

/**
 *
 * @author web
 */
public class Controller extends SelectorComposer<Component>{
    
    @Listen("onMapClick = #gmaps")
    public void onMapClick(MapMouseEvent event) {
        System.err.println("HIER");
        Gmarker gmarker = event.getGmarker();
        if(gmarker != null) {
            gmarker.setOpen(true);
        }
    }
}
