package salam.raj;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    TextView tvspeed;
    public LocationRequest mlocationrequest;
    Button btstart,btset;
    public boolean isitfirsttime =true,isitsecondtime = false;
    double latitude,longitude;
    double lat1,lon1,lat2,lon2;
    public Location mlastlocation;
    public GoogleApiClient mApiClient;
    AlertDialog.Builder dialog;
    public ProgressDialog pLoading;
    FirebaseDatabase fbdatabase;
    DatabaseReference dbref;
    float distance;
    String phoneno;
    SharedPreferences sdf;
    float speed;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sdf = getApplicationContext().getSharedPreferences("users",0);
        editor = sdf.edit();

        phoneno = sdf.getString("userphoneno",null);


        fbdatabase = FirebaseDatabase.getInstance();
        dbref = fbdatabase.getReference("user_speed_info");

        pLoading = new ProgressDialog(this);

        dialog = new AlertDialog.Builder(this);

        tvspeed = (TextView) findViewById(R.id.tv_speed);
        btstart = (Button) findViewById(R.id.bt_start);
        btset = (Button) findViewById(R.id.btsetfire);


        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mlocationrequest = new LocationRequest();
        mlocationrequest.setInterval(2000);
        mlocationrequest.setFastestInterval(2000);
        mlocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        btstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getlocation();

            }
        });

        btset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbref.child(phoneno).push().setValue((int)speed);
            }
        });


    }

    private void getlocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Sara Needs Your Persmission to Get Locations");
            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

        }else {
            pLoading.setMessage("Getting the Location\nPlease Wait...");
            pLoading.setCancelable(false);
            pLoading.show();
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mlocationrequest, this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mApiClient!=null){
            mApiClient.connect();
        }
    }


       @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("Connection Status","Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e("Connection status",connectionResult.getErrorMessage());

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mlastlocation = location;
        if (mlastlocation!=null){
           dispalylocation(mlastlocation);
        }
    }

    private void dispalylocation(Location mlastlocation) {
        pLoading.dismiss();
        latitude = mlastlocation.getLatitude();
        longitude = mlastlocation.getLongitude();
        Log.e("co-ordinates",latitude+"\n"+longitude);
       calculatedistance(latitude,longitude);
    }

    private void calculatedistance(double latitude, double longitude) {
        if (isitfirsttime){
             lat1 = latitude;
             lon1 = longitude;
            isitfirsttime =false;
            isitsecondtime = true;
            Log.e("status","First Time");
        }else if (isitsecondtime){
             lat2 = latitude;
             lon2 = longitude;
            Log.e("status","Second Time");
            isitsecondtime = false;
           distance =  gettingdistance(lat1,lon1,lat2,lon2);
            //tvspeed.setText("Distance"+distance);
            calculatespeed(distance);
            Log.e("distance",String.valueOf(distance));
        }

        if (!isitsecondtime){
            lat1 = lat2;
            lon1 = lon2;
            lat2 = latitude;
            lon2 = longitude;
            Log.e("status","Third Time");
            distance = gettingdistance(lat1,lon1,lat2,lon2);
            //tvspeed.setText("Distance"+distance);
            calculatespeed(distance);
            Log.e("distance",String.valueOf(distance));
        }
    }

    private void calculatespeed(float distance) {
        if (distance>1000){
            distance = distance/1000;
        }
        speed = distance/2;
        Log.e("speed",String.valueOf(speed));
        tvspeed.setText("Speed "+(int)speed);
    }

    private float gettingdistance(double lat1, double lon1, double lat2, double lon2) {
        Log.e("co-ords",lat1+" "+lat2+"\n"+lon1+" "+lon2);

        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat2-lat1);
        double lngDiff = Math.toRadians(lon2-lon1);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();


    }
}
