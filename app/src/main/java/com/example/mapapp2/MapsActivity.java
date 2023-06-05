package com.example.mapapp2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mapapp2.Point;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_FILE_PICKER = 2;

    private Button loadLocationsButton;

    // Define the sets of points
    private List<Point> set1Points;
    private List<Point> set2Points;
    private List<Point> set3Points;

    // Define the selected set of points
    private List<Point> selectedPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
//                for (Location location : locationResult.getLocations()) {
//                    // Update map with current location
//                    updateMapWithLocation(location);
//                }
            }
        };

        // Initialize the sets of points
        set1Points = new ArrayList<>();
        set1Points.add(new Point(37.1234, -120.6500, "Point 1"));  // Within 10 miles of (37.2058, -120.7697)
        set1Points.add(new Point(37.1350, -120.7600, "Point 2"));  // Within 10 miles of (37.2058, -120.7697)
        set1Points.add(new Point(37.1500, -120.7800, "Point 3"));  // Within 10 miles of (37.2058, -120.7697)

        set2Points = new ArrayList<>();
        set2Points.add(new Point(37.1750, -120.8000, "Point 4"));  // Within 10 miles of (37.2058, -120.7697)
        set2Points.add(new Point(37.1900, -120.8100, "Point 5"));  // Within 10 miles of (37.2058, -120.7697)
        set2Points.add(new Point(37.2000, -120.8200, "Point 6"));  // Within 10 miles of (37.2058, -120.7697)

        set3Points = new ArrayList<>();
        set3Points.add(new Point(37.2100, -120.8300, "Point 7"));  // Within 10 miles of (37.2058, -120.7697)
        set3Points.add(new Point(37.2200, -120.8400, "Point 8"));  // Within 10 miles of (37.2058, -120.7697)
        set3Points.add(new Point(37.2300, -120.8500, "Point 9"));  // Within 10 miles of (37.2058, -120.7697)

// Display or use the points as per your requirement

        selectedPoints = set1Points; // Set the initial selected points

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        loadLocationsButton = findViewById(R.id.loadLocationsButton);
        loadLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Check if last known location is available
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateMapWithLocation(location);
                        }
                    }
                });

        // Start location updates
        startLocationUpdates();
    }


    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void updateMapWithLocation(Location location) {
        if (googleMap != null) {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));

            // Add a marker at the current location
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Current Location"));
        }
    }

    private void openFilePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a location set");

        String[] locationSets = {"Location Set 1", "Location Set 2", "Location Set 3"};

        builder.setItems(locationSets, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadPointsForSet(which);
            }
        });

        builder.show();
    }

    private void loadPointsForSet(int setIndex) {
        // Load the points based on the selected set index

        // Example code:
        switch (setIndex) {
            case 0:
                loadPoints(set1Points);
                break;
            case 1:
                loadPoints(set2Points);
                break;
            case 2:
                loadPoints(set3Points);
                break;
        }
    }

    private void loadPoints(List<Point> points) {
        // Clear previous markers
        googleMap.clear();

        // Add markers for each point
        for (Point point : points) {
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng));
        }
    }



    private void loadPointsFromFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                List<Point> selectedPoints = null;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#set1")) {
                        selectedPoints = set1Points;
                    } else if (line.startsWith("#set2")) {
                        selectedPoints = set2Points;
                    } else {
                        if (selectedPoints != null) {
                            String[] coordinates = line.split(",");
                            if (coordinates.length == 2) {
                                double latitude = Double.parseDouble(coordinates[0]);
                                double longitude = Double.parseDouble(coordinates[1]);
                                selectedPoints.add(new Point(latitude, longitude));
                            }
                        }
                    }
                }

                if (googleMap != null && selectedPoints != null) {
                    showPointsOnMap(selectedPoints);
                }

                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPointsOnMap(List<Point> points) {
        googleMap.clear(); // Clear existing markers

        for (Point point : points) {
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng));
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
            }
        }
    }

    private void displayPointsOnMap() {
        googleMap.clear();
        for (Point point : selectedPoints) {
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(point.getName());
            googleMap.addMarker(markerOptions);
        }
    }

    private void switchPointSet(List<Point> newPoints) {
        selectedPoints = newPoints;
        displayPointsOnMap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    List<Point> importedPoints = readPointsFromFile(uri);
                    if (importedPoints != null) {
                        switchPointSet(importedPoints);
                    }
                }
            }
        }
    }

    // Assume you have a method to read points from a file and return a List<Point>
    private List<Point> readPointsFromFile(Uri uri) {
        // Read points from the file and return a List<Point>
        // Replace this with your own implementation
        return null;
    }
}
