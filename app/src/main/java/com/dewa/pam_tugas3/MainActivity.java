package com.dewa.pam_tugas3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private GoogleMap gMap, orderMap;
    private Marker selectedMarker;
    private LatLng selectedPlace;

    private FirebaseFirestore db;

    private TextView txtOrderId, txtSelectedPlace;
    private EditText editTextName;
    private Button btnEditOrder, btnOrder;

    private boolean isNewOrder = true;

    // Untuk membuat Current Location
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_ADDRESS = "extra_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtOrderId = findViewById(R.id.txt_orderId);
        txtSelectedPlace = findViewById(R.id.txt_selectedPlace);
        editTextName = findViewById(R.id.editTxt_name);
        btnEditOrder = findViewById(R.id.btn_editOrder);
        btnOrder = findViewById(R.id.btn_order);

        db = FirebaseFirestore.getInstance();

        String nameListView = getIntent().getStringExtra(EXTRA_NAME);
        String addressListView = getIntent().getStringExtra(EXTRA_ADDRESS);

        btnOrder.setOnClickListener(view -> { saveOrder(); });
        btnEditOrder.setOnClickListener(view -> { updateOrder(); });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Location();

        if (nameListView != null && addressListView != null){
            editTextName.setText(nameListView);
            txtSelectedPlace.setText(addressListView);
        }
    }

    @SuppressLint("MissingPermission")
    private void Location() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                            +" "+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        orderMap = googleMap;

        String orderName = getIntent().getStringExtra(EXTRA_NAME);
        String orderAddress = getIntent().getStringExtra(EXTRA_ADDRESS);

        if (orderName != null && orderAddress != null) {
            db.collection("orders").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> listOrder = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : listOrder) {
                                String cekOrderName = d.get("name").toString();
                                String cekOrderAddress = d.get("address").toString();

                                double lat = (Double) d.get("lat");
                                double lng = (Double) d.get("lng");

                                if (orderName.equals(cekOrderName) && orderAddress.equals(cekOrderAddress)) {
                                    LatLng orderLatLng = new LatLng(lat, lng);
                                    MarkerOptions markerOptions = new MarkerOptions().position(orderLatLng).title(cekOrderName);
                                    orderMap.animateCamera(CameraUpdateFactory.newLatLng(orderLatLng));
                                    orderMap.animateCamera(CameraUpdateFactory.newLatLngZoom(orderLatLng, 17.0f));
                                    selectedMarker = orderMap.addMarker(markerOptions);
                                } else {
                                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                    MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title("I am here");
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    gMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17.0f));
                                    selectedMarker = gMap.addMarker(markerOptions);
                                }
                            }
                        }
                    });
        }

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title("I am here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        gMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17.0f));
        selectedMarker = gMap.addMarker(markerOptions);

        gMap.setOnMapClickListener(this);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location();
                }
                break;
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        selectedPlace = latLng;
        selectedMarker.setPosition(selectedPlace);
        gMap.animateCamera(CameraUpdateFactory.newLatLng(selectedPlace));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(selectedPlace.latitude,
                    selectedPlace.longitude, 1);
            if (addresses != null) {
                Address place = addresses.get(0);
                StringBuilder street = new StringBuilder();

                for (int i=0; i <= place.getMaxAddressLineIndex(); i++) {
                    street.append(place.getAddressLine(i)).append("\n");
                }

                txtSelectedPlace.setText(street.toString());
            }
            else {
                Toast.makeText(this, "Could not find Address!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Error get Address!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrder() {
        Map<String, Object> order = new HashMap<>();

        String name = editTextName.getText().toString();
        String orderId = txtOrderId.getText().toString();

        order.put("name", name);
        order.put("address", txtSelectedPlace.getText().toString());
        order.put("createdDate", new Date());
        order.put("lat", selectedPlace.latitude);
        order.put("lng", selectedPlace.longitude);


        if (isNewOrder) {
            db.collection("orders")
                    .add(order)
                    .addOnSuccessListener(documentReference -> {
                        editTextName.setText("");
                        txtSelectedPlace.setText("Pilih tempat");
                        txtOrderId.setText(documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal tambah data order",
                                Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            db.collection("orders").document(orderId)
                    .set(order)
                    .addOnSuccessListener(unused -> {
                        editTextName.setText("");
                        txtSelectedPlace.setText("");
                        txtOrderId.setText(orderId);

                        isNewOrder = true;
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal ubah data order",
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateOrder() {
        isNewOrder = false;

        String orderId = txtOrderId.getText().toString();
        DocumentReference order = db.collection("orders").document(orderId);
        order.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.get("name").toString();
                    String address = document.get("address").toString();
                    Double lat = (Double) document.get("lat");
                    Double lng = (Double) document.get("lng");

                    editTextName.setText(name);
                    txtSelectedPlace.setText(address);

                    LatLng resultPlace = new LatLng(lat, lng);
                    selectedPlace = resultPlace;
                    selectedMarker.setPosition(selectedPlace);
                    gMap.animateCamera(CameraUpdateFactory.newLatLng(selectedPlace));
                }
                else {
                    isNewOrder = true;
                    Toast.makeText(this, "Document does not exist!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Unable to read the db!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listorders, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setMode(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void setMode(int itemId) {
        switch (itemId) {
            case R.id.item_menu:
                Intent moveIntent = new Intent(MainActivity.this,
                        ListViewOrdersActivity.class);
                startActivity(moveIntent);
        }
    }
}