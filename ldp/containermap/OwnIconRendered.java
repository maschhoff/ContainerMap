package ldp.containermap;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static ldp.containermap.R.id.map;

/**
 * Created by web on 08.11.2016.
 *
 * http://stackoverflow.com/questions/23658561/how-to-set-my-own-icon-for-markers-in-clusterer-in-google-maps
 * https://github.com/googlemaps/android-maps-utils/blob/master/demo/src/com/google/maps/android/utils/demo/CustomMarkerClusteringDemoActivity.java
 * https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/clustering/view/DefaultClusterRenderer.java
 */



public class OwnIconRendered extends DefaultClusterRenderer<ContainerLocation> {


    public OwnIconRendered(Context context, GoogleMap map, ClusterManager<ContainerLocation> clusterManager){
        super(context,map,clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ContainerLocation item,
                                               MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(item.getAgpImage()));
        markerOptions.title(item.getAgpString());
    }

}
