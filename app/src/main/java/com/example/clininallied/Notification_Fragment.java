package com.example.clininallied;


import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Polyline;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.codebyashish.googledirectionapi.AbstractRouting;
import com.codebyashish.googledirectionapi.ErrorHandling;
import com.codebyashish.googledirectionapi.RouteDrawing;
import com.codebyashish.googledirectionapi.RouteInfoModel;
import com.codebyashish.googledirectionapi.RouteListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;


import java.util.ArrayList;
import java.util.List;

public class Notification_Fragment extends Fragment implements OnMapReadyCallback, RouteListener {
    private GoogleMap myMap;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private LatLng destinationCords;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final LatLng AlliedClinix = new LatLng(34.00104282936619, 72.93708901519223);
    private ArrayList<Polyline> polylines = null;

    public Notification_Fragment() {
        // Required empty public constructor
    }

    public static Notification_Fragment newInstance(String param1, String param2) {
        Notification_Fragment fragment = new Notification_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Set up the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        ImageButton button = view.findViewById(R.id.menumap);

        PopupMenu popupMenu = new PopupMenu(requireContext(), button);
        popupMenu.getMenuInflater().inflate(R.menu.map_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.normal) {
                    myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    return true;
                } else if (id == R.id.satellite) {
                    myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                } else if (id == R.id.hybrid) {
                    myMap.setMapType(MAP_TYPE_HYBRID);
                    return true;
                } else if (id == R.id.terrain) {
                    myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                    return true;
                } else {
                    return false;
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
        Places.initialize(requireContext(), getString(R.string.google_app));
        autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.auto_complete);
        autocompleteSupportFragment.setPlaceFields(List.of(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(requireContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destinationCords = place.getLatLng();
                zoomOnLatlng(destinationCords);

            }
        });

        return view;
    }

    private void zoomOnLatlng(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(12f).build();
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        CameraPosition cameraPosition = new CameraPosition(AlliedClinix, 15f, 0f, 0f);
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        myMap.setMyLocationEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.setBuildingsEnabled(true);

        myMap.addMarker(new MarkerOptions()
                .position(AlliedClinix).
                title("Allied Clinix")
                .icon(BitmapFromVector(
                        requireContext(),
                        R.drawable.hospital_svgrepo_com
                )));


        myMap.setTrafficEnabled(true);
        getMyLocation();

    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                destinationCords = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d( "onSuccess", "onSuccess: "+destinationCords);
                getRoute(destinationCords,AlliedClinix);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void getRoute(LatLng destinationCords , LatLng originCords) {
        if (destinationCords == null || originCords == null) {
            Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_LONG).show();
            Log.e("mapCords", " latlngs are null");
        } else {
            RouteDrawing routeDrawing = new RouteDrawing.Builder()
                    .context(requireContext())  // pass your activity or fragment's context
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this).alternativeRoutes(true)
                    .waypoints(destinationCords, originCords)
                    .build();
            //noinspection deprecation
            routeDrawing.execute();
        }
    }

    private BitmapDescriptor
    BitmapFromVector(Context context, int vectorResId)
    {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRouteFailure(ErrorHandling e) {
        Toast.makeText(requireContext(), "Fsilure" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteStart() {
        Toast.makeText(requireContext(), "Route Started", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> routeInfoModelArrayList, int routeIndexing) {
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<Polyline> polylines = new ArrayList<>();
        for (int i = 0; i < routeInfoModelArrayList.size(); i++) {
            if (i == routeIndexing) {
                Log.e("TAG", "onRoutingSuccess: routeIndexing" + routeIndexing);
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(12);
                polylineOptions.addAll(routeInfoModelArrayList.get(routeIndexing).getPoints());
                polylineOptions.startCap(new RoundCap());
                polylineOptions.endCap(new RoundCap());
                Polyline polyline = myMap.addPolyline(polylineOptions);
                polylines.add(polyline);
            }
        }

    }

    @Override
    public void onRouteCancelled() {
        Toast.makeText(requireContext(), "Route Cancelled", Toast.LENGTH_SHORT).show();
    }
}
