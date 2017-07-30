package salam.raj;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements android.location.LocationListener {

    boolean isgpsenabled, isnetworkenabled,isitfirst = true,isitsecond = false;
    LocationManager manager;
    Location location;
    double latitude, longitude,latitude2,longitude2,lat1,lat2,lon1,lon2;
    TextView tvspeed;
    Button btstart;
    float distance;
    AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new AlertDialog.Builder(this);

        tvspeed = (TextView) findViewById(R.id.tv_speed);
        btstart = (Button) findViewById(R.id.bt_start);


        manager = (LocationManager) this.getSystemService(LOCATION_SERVICE);


        btstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    checkinggps();


            }
        });


    }

    private void checkinggps() {
        isgpsenabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isnetworkenabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.e("gpsenabled", String.valueOf(isgpsenabled));
        Log.e("networkenabled", String.valueOf(isnetworkenabled));
        if (isgpsenabled && isnetworkenabled) {
            checkpermissions();

        } else {
            dialog.setTitle("Alert");
            dialog.setMessage("SARA Needs GPS To Continue");
            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialog.show();
            //Toast.makeText(MainActivity.this, "Please enable gps to start", Toast.LENGTH_LONG).show();
        }
    }

    private void checkpermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            dialog.setTitle("Alert");
            dialog.setMessage("SARA Need Permissions To Continue");
            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialog.show();
        } else {
            getlocation();
        }

    }


    private void getlocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (isgpsenabled){
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }else if (isnetworkenabled){
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else {
            dialog.setTitle("Alert");
            dialog.setMessage("SARA Needs GPS To conitnue\n Please Enable It");
            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialog.show();
        }

        if (isitfirst){
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.e("latitude and longitudes",latitude+"\n"+longitude);

            if (latitude!=0 && longitude!=0){
                isitfirst = false;
                checkinggps();
            }
        }else{
            latitude2 = location.getLatitude();
            longitude2 = location.getLongitude();
            calculatedistance(latitude2,longitude2);
            checkinggps();
        }





    }

    private void calculatedistance(double latitude2, double longitude2) {
        if (!isitsecond){
             lat1 = latitude;
             lon1 = longitude;

             lat2 = latitude2;
             lon2 = longitude2;

            isitsecond = true;
            Log.e("co-oridanates1",lat1+" "+lon1+"\n"+lat2+" "+lon2);
            distance = gettingdistance(lat1,lon1,lat2,lon2);

            tvspeed.setText("Distance:- "+distance);
            Log.e("Distance", String.valueOf(distance));
        }else {
            lat1 = lat2;
            lon1 = lon2;

            lat2 = latitude2;
            lon2 = longitude2;
            Log.e("co-oridanates2",lat1+" "+lon1+"\n"+lat2+" "+lon2);
            distance = gettingdistance(lat1,lon1,lat2,lon2);
            tvspeed.setText("Distance:- "+distance);
            Log.e("Distance", String.valueOf(distance));
        }
    }

    private float gettingdistance(double lat1, double lon1, double lat2, double lon2) {
        double distance = 0;

        lat2 = 52.527204;
        lon2 = 13.398167;




        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat2-lat1);
        double lngDiff = Math.toRadians(lon2-lon1);
        //Log.e("difference",latDiff+"\n"+lngDiff);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
       // Log.e("Calculation",String.valueOf(a));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        //Log.e("Calc",String.valueOf(c));
        distance = earthRadius * c;

        int meterConversion = 1609;

        Log.e("Dist", String.valueOf(distance*meterConversion));

        return new Float(distance * meterConversion).floatValue();




    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
