package sg.edu.rp.c347.p09gettingmylocations;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnStop, btnCheck;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient client;
    TextView tvLat, tvLng;
    String folderLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.buttonStart);
        btnStop = findViewById(R.id.buttonStop);
        btnCheck = findViewById(R.id.buttonCheck);

        tvLat = findViewById(R.id.textViewLat);
        tvLng = findViewById(R.id.textViewLng);


        client = LocationServices.getFusedLocationProviderClient(this);

        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mySavedLocations";



                if (checkPermission()) {

                    //make folder
                    File folder = new File(folderLocation);
                    if (folder.exists() == false) {
                        boolean result = folder.mkdir();
                        if (result == true) {
                            Log.d("File Read/Write", "Folder created");
                        }
                    }



                    //get last location
                    Task<Location> task = client.getLastLocation();

                    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some reare situations this can be null
                            if (location != null) {

                                tvLat.setText(String.valueOf(location.getLatitude()));
                                tvLng.setText(String.valueOf(location.getLongitude()));

                            } else {
                                String msg = "No last known lcoation";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    String msg = "Permission not granted to retrieve location info";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

                }




        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MyService.class);
                startService(i);


                if (checkPermission()) {
                    mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(10000);
                    mLocationRequest.setFastestInterval(5000);
                    mLocationRequest.setSmallestDisplacement(100);

                    mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {

                            if (locationResult != null) {
                                Location data = locationResult.getLastLocation();
                                double lat = data.getLatitude();
                                double lng = data.getLongitude();

                                String msg = "New LOC DETECTED\n LAT : " + lat +
                                        " Lng : " + lng;
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();


                                try {
                                    String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath()
                                            + "/mySavedLocations";

                                    File targetFile = new File(folderLocation, "locations.txt");

                                    FileWriter writer = new FileWriter(targetFile, true);
                                    //false will overwrite with the first line
                                    //true adds to the line
                                    writer.write("" + lat + ", " + lng + "\n");
                                    writer.flush();
                                    writer.close();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "Failed to write!",Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }




                            }else {
                                String msg = "No last known lcoation";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                    };



                    client.requestLocationUpdates(mLocationRequest, mLocationCallback,null);

                }
                else {
                    String msg = "Permission not granted to retrieve location info";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MyService.class);
                stopService(i);

                client.removeLocationUpdates(mLocationCallback);
            }
        });


        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mySavedLocations";
                File targetFile = new File(folderLocation, "locations.txt");

                if (targetFile.exists() == true) {
                    String data = "";

                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line!= null) {
                            data += line + "\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to read!",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();

                    }

                    Log.d("Content",data);
                    Toast.makeText(MainActivity.this, data,Toast.LENGTH_LONG).show();
                }
            }
        });


    }




    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionCheck_WRITEEXT = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_READEXT = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);


        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
        || permissionCheck_WRITEEXT== PermissionChecker.PERMISSION_GRANTED
        || permissionCheck_READEXT == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


}
