package ldp.containermap;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.ResultCallback;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;

import android.net.Uri;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;

import android.support.v4.content.ContextCompat;
import android.widget.ViewFlipper;

import java.util.Set;

import static ldp.containermap.R.raw.bochum;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private Marker currentPositionMarker;
    private InterstitialAd mInterstitialAd;
    private Marker mMarker;
    private AdView mAdView;
    private ViewFlipper vf;
    private SupportMapFragment mapFragment;
    private ClusterManager<ContainerLocation> mClusterManager;

    private static final String LOG_TAG = "MainActivity";
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_NRW = new LatLngBounds(
            new LatLng(51.4, 7.3), new LatLng(51.4, 7.3));


    private static boolean proVersion = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("TAG", "onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Overlay Button

        vf = (ViewFlipper) findViewById(R.id.vf);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAutocompleteTextView.setHint("Auf der Karte suchen...");
                mAutocompleteTextView.setText("");

                mAutocompleteTextView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mAutocompleteTextView, InputMethodManager.SHOW_IMPLICIT);

                if (!proVersion) {
                    vf.showNext();
                }
            }
        });


        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (location != null) {
                    setCurrentPosition(location, true);
                }
            }
        });

        //Map Fragment


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Check GPS Permissions

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    11);
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    12);
        }


        //Pro Version
        if (!proVersion) {
            initAds();
        } else {
            vf.showNext();
        }


        //Autocomplete

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .build();


        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);


        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_NRW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);


        //Connect GoogleAPI / Load LocationManager
        checkLocationManager();


    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);

            Location searchLocation = new Location("SeachLocation");
            searchLocation.setLatitude(place.getLatLng().latitude);
            searchLocation.setLongitude(place.getLatLng().longitude);
            setCurrentPosition(searchLocation, true);

            if (!proVersion) {
                vf.showNext();
            }


        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

        initLocationManager();


        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }


    private void initAds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                if (mMarker != null) {
                    goIntent();
                }
            }
        });

        requestNewInterstitial();


        MobileAds.initialize(this, "ca-app-pub-2604144482293760~7930391103");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("7A4ABB3EB44C0FBAF5286DF6FFB56A30").build();
        mAdView.loadAd(adRequest);

    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("7A4ABB3EB44C0FBAF5286DF6FFB56A30")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        Log.e("TAG", "onRequestPermissionsResult");
        switch (requestCode) {
            case 11: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationManager();
                } else {
                    // permission denied
                }
                break;
            }

            case 12: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationManager();
                } else {
                    // permission denied
                }
                break;
            }

        }
    }


    private void checkLocationManager() {
        Log.e("TAG", "checkLocationManager");
        mGoogleApiClient.connect();

    }

    private void initLocationManager() {
        Log.e("TAG", "initLocationManager()");

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Berechtigungen zur Lokalisierung fehlen!", Toast.LENGTH_LONG).show();
            Log.e("TAG", "NO PERMISSION!");
            return;
        }

        if (locationManager == null) {
            Log.e("TAG", "initLocationManager...");
            // Get the location manager
            locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            location = locationManager.getLastKnownLocation(provider);
        }


        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mMap != null && location != null) {
            setCurrentPosition(location, true);
        } else {
            Log.e("DEBUG", "LOCATION OR MAP NULL!!!!!!!!");
        }


    }


    private void goIntent() {
        String currentPosition = "";

        if (location != null) {
            currentPosition = "" + location.getLatitude() + "," + location.getLongitude();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d" +
                        "&saddr=" + currentPosition + "&daddr=" + mMarker.getPosition().latitude + "," + mMarker.getPosition().longitude + "&hl=zh&t=m&dirflg=d"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("TAG", "onMapReady");

        mMap = googleMap;


        // UI Settings
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //STYLE

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }


        // INFO WINDOW - CUSTOM mit Navigation

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker args) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(final Marker args) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.custom_window, null);


                TextView title = (TextView) v.findViewById(R.id.txtInfoWindowTitle);
                title.setText(args.getTitle());

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {

                        mMarker = marker;

                        if (!proVersion && mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            goIntent();
                        }

                    }
                });

                // Returning the view containing InfoWindow contents
                return v;

            }
        });


        // Add a marker in Sydney and move the camera
        LatLng bochum = new LatLng(51.48, 7.21);
        if (location != null) {
            bochum = new LatLng(location.getLatitude(), location.getLongitude());
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bochum, 15));

        setUpClusterer();

/*
        for (ContainerLocation cl : Locations.containerLocations) {
            mMap.addMarker(new MarkerOptions().position(cl.getLocation()).title(cl.getAgpString()).icon(BitmapDescriptorFactory.fromResource(cl.getAgpImage())));
        }
        */


    }


    private void setUpClusterer() {
        //https://developers.google.com/maps/documentation/android-api/utility/marker-clustering?hl=de
        mClusterManager = new ClusterManager<ContainerLocation>(this, mMap);
        mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        addItems();
    }

    private void addItems() {
        // Add ten cluster items in close proximity, for purposes of this example.
        for (ContainerLocation cl : Locations.containerLocations) {
            //   mMap.addMarker(new MarkerOptions().position(cl.getLocation()).title(cl.getAgpString()).icon(BitmapDescriptorFactory.fromResource(cl.getAgpImage())));
            mClusterManager.addItem(cl);
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("TAG", "onStatusChanged");

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("TAG", "onProviderEnabled");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("TAG", "onProviderDisabled");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e("DEBUG", "onLocationChanged");


            this.location = location;
            setCurrentPosition(this.location, false);


    }

    private void setCurrentPosition(Location newLocation, boolean move) {


        if (mMap != null && newLocation != null) {
            LatLng position = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

            //Alten marker entfernen
            if (currentPositionMarker != null) {
                currentPositionMarker.remove();
            }

            currentPositionMarker = mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

            if (move) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            }
        }
    }

}


