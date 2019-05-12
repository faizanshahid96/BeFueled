package com.example.faizans.befueled.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.faizans.befueled.MainActivity;
import com.example.faizans.befueled.R;
import com.example.faizans.befueled.Utils.FuelRequestInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.faizans.befueled.Utils.Constants.FUEL_PRICE;
import static com.example.faizans.befueled.Utils.Constants.MAPVIEW_BUNDLE_KEY;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "UserListFragment";
    //vars
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Button mBtnfuelrequest, mBtnCancelRqst;
    FuelRequestInfo fuelRequestInfo;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    private MapView mMapView;
    private LatLng pickupLocation;
    private LatLng midLatLng = null;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private int i = 0;
    private boolean mIsOrderPlace = false;
    private String mUserID;
    private TextView textFuelprice;
    double radius = 1;
    boolean isDriverFound = false;
    private String driverId;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.map);
        mBtnfuelrequest = view.findViewById(R.id.btn_fuel_request);
        mBtnfuelrequest.setOnClickListener(this);
        textFuelprice = view.findViewById(R.id.text_fuel_price);
        textFuelprice.setText(String.valueOf(FUEL_PRICE));
        buildGoogleApiClient();
        initGoogleMap(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mUserID = mAuth.getCurrentUser().getUid();
        mBtnCancelRqst = view.findViewById(R.id.btn_cancel);
        mBtnCancelRqst.setOnClickListener(this);
        reference = FirebaseDatabase.getInstance().getReference();
        FirebaseDatabase.getInstance().getReference().child("customerRequestInfo")
                .child(mUserID).child("isorderplaced").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue(Boolean.class)) {

                        mBtnfuelrequest.setText("On Our Way");
//                        loadDriverLocation();
                        mBtnCancelRqst.setVisibility(view.VISIBLE);
                        mIsOrderPlace = dataSnapshot.getValue(Boolean.class);
                        mBtnfuelrequest.setClickable(false);
                        Log.d(TAG, "onDataChange:exception " + dataSnapshot.getValue(Boolean.class) + " " + mIsOrderPlace);
                    } else {

                        mBtnfuelrequest.setText("BHARO");
                        mMap.clear();
                        mBtnfuelrequest.setClickable(true);
                        mBtnCancelRqst.setVisibility(view.INVISIBLE);
//                        mIsOrderPlace = false;
                    }
                } catch (NullPointerException npe) {
                    mBtnfuelrequest.setText("BHARO");
                    mBtnfuelrequest.setClickable(true);
//                    mIsOrderPlace = false;
                    mMap.clear();
                    mBtnCancelRqst.setVisibility(view.INVISIBLE);
                    Log.d(TAG, "onDataChange:exception " + npe);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }


//    private void loadDriverLocation() {
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("driverAvailable");
//        GeoFire gfdriver = new GeoFire(ref);
//        Log.d(TAG, "loadDriverLocation:In " + mLastLocation);
//        GeoQuery geoQuery = gfdriver.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
//        geoQuery.removeAllListeners();
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                if (!isDriverFound) {
//                    isDriverFound = true;
//                    driverId = key;
//                    Log.d(TAG, "onKeyEntered:driverFound: " + key);
////                    Toast.makeText(getContext(), "DriverFound", Toast.LENGTH_SHORT).show();
//                    int height = 100;
//                    int width = 100;
//                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_truck);
//                    Bitmap b = bitmapdraw.getBitmap();
//                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//
//                    mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(location.latitude, location.longitude))
//                            .flat(true)
//                            .title("Driver")
//                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
//                }
//
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//                if (!isDriverFound) {
//                    radius++;
//                    loadDriverLocation();
//                }
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//
//
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_fuel_request) {
            if (midLatLng != null) {
                Toast.makeText(getActivity(), "pickupLocation: " + midLatLng.latitude
                        + "lat" + midLatLng.longitude, Toast.LENGTH_SHORT).show();
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            fuelRequestInfo = new FuelRequestInfo(userId, midLatLng);
            FuelRequestInfo.setUserID(userId);
            FuelRequestInfo.setLatLng(midLatLng);


//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
//            GeoFire geoFire = new GeoFire(ref);
//
//            geoFire.setLocation(userId, new GeoLocation(midLatLng.latitude, midLatLng.longitude), new GeoFire.CompletionListener() {
//
//                @Override
//                public void onComplete(String key, DatabaseError error) {
//                    Log.d(TAG, "key:" + key);
//
//                }
//            });


//            pickupLocation = new LatLng(midLatLng.latitude, midLatLng.longitude);
//            mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here"));

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new FuelRequestFragment());
            transaction.addToBackStack("fragment_container");
            transaction.commit();
        }
        if (v.getId() == R.id.btn_cancel) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to cancel this request?")
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            reference.child("customerRequestInfo").child(mUserID).removeValue(null);
                            reference.child("customerRequest").child(mUserID).removeValue(null);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        super.onViewCreated(view, savedInstanceState);

    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                midLatLng = mMap.getCameraPosition().target;
                // Toast.makeText(getActivity(), "Lat is : " + midLatLng.latitude + "\n" + "Lng is : " + midLatLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });

        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
            }
        });


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("SSSS", "-------------------");
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        if (mIsOrderPlace) {
//            loadDriverLocation();
            Log.d(TAG, "onLocationChanged:in " + mLastLocation);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d(TAG, "InOnconnected");
        onMapReady(mMap);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        loadDriverLocation();
//        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    Log.d(TAG, "onSuccess: " + "latitude" + location.getLatitude() + "longitude " + location.getLongitude());
////                    Toast.makeText(getContext(), ""+ i++ +"latitude"+location.getLatitude(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                for (Location location : locationResult.getLocations()) {
//                    // Update UI with location data
//                    Toast.makeText(getContext(), "" + i++ + "latitude" + location.getLatitude(), Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        };
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}