package Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedometer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Other.LoggedAccHandler;
import Other.RaceTimesHandler;

public class RaceModeActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;

    private TextView raceModeSpeedometerTextView;
    private TextView raceModeInfo;
    private TextView raceModeTimerTextView;
    private TextView distanceMeter;
    private ListView raceModeTimesListView;

    private Button saveLapTimesButton;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private Handler handler = new Handler();

    private long startTime = 0L;
    private long timeInMs = 0L;
    private long updateTime = 0L;
    private long timeSwapBuff = 0L;

    private int sec;
    private int min;
    private int ms;

    private double startPointLatitude;
    private double startPointLongitude;
    private double distance;
    private double trackLength;
    private String car;
    private String user;
    private String track;

    private static final String TRACK_LENGTH = "TRACK_LENGTH";
    private static final String CHOSEN_CAR = "CHOSEN_CAR";
    private static final String CHOSEN_TRACK = "CHOSEN_TRACK";

    private Context context = this;

    String[] ListElements = new String[]{};
    List<String> ListElementsArrayList;
    ArrayAdapter<String> adapter;

    Runnable updateTimeThread = new Runnable() {
        @Override
        public void run() {
            timeInMs = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMs;
            sec = (int) updateTime / 1000;
            min = sec / 60;
            sec %= 60;
            ms = (int) updateTime % 1000;
            raceModeTimerTextView.setText("" + String.format("%02d", min) + ":" + String.format("%02d", sec) + ":" + String.format("%03d", ms));
            handler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.racemode_activity);
        Bundle bundle = getIntent().getExtras();
        trackLength = Double.parseDouble(bundle.getString(TRACK_LENGTH));
        car = bundle.getString(CHOSEN_CAR);
        track = bundle.getString(CHOSEN_TRACK);
        LoggedAccHandler lah = new LoggedAccHandler();
        user = lah.getLoggedUserName();
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            raceModeInfo.setTextSize(20f);
            raceModeInfo.setText(R.string.gpsFail);
        }
    }

    private void initView() {
        raceModeSpeedometerTextView = findViewById(R.id.raceModeSpeedometerTextView);
        raceModeTimesListView = findViewById(R.id.raceModeTimesListView);
        raceModeTimerTextView = findViewById(R.id.raceModeTimerTextView);
        raceModeInfo = findViewById(R.id.raceModeInfo);
        distanceMeter = findViewById(R.id.distanceMeter);
        saveLapTimesButton = findViewById(R.id.saveLapTimesButton);

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));


        adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                ListElementsArrayList
        );

        raceModeTimesListView.setAdapter(adapter);

        saveLapTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("RaceTimes");

                String nTrackLength = String.valueOf(trackLength);
                String[] times = ListElementsArrayList.toArray(new String[0]);
                // TODO: 08.11.2020 pobrac dane odnosnie samochodu uzytkownik i wybrany tor
                RaceTimesHandler handler = new RaceTimesHandler(times, car, user, track, nTrackLength);
                databaseReference.child(user).setValue(handler);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        permissionGranted();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionGranted();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkGPSConnection();
        }
    }

    @SuppressLint("MissingPermission")
    private void checkGPSConnection() {
        permissionGranted();
        Toast.makeText(context, "Waiting for GPS connection...", Toast.LENGTH_LONG).show();
    }

    public void permissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        // TODO: 25.10.2020 wziac current location i policzyc dystans pomiedy nim a starym dystansem i powiekszyc dystans
        if (location != null) {
            float speed = location.getSpeed();
            float convertedSpeedToKmH = speed * 3600 / 1000;
            int nCurrentSpeed = (int) convertedSpeedToKmH;
            String nStrCurrentSpeed = String.valueOf(nCurrentSpeed);
            raceModeSpeedometerTextView.setText(nStrCurrentSpeed);
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            traveledDistance(currentLatitude, currentLongitude);

            if (convertedSpeedToKmH >= 10 && convertedSpeedToKmH <= 15) {
                startTimer();
                setStartPoint(location);
            } else if (convertedSpeedToKmH >= 7 && convertedSpeedToKmH <= 14) {
                stopTimer();
            }
        }
    }

    private double traveledDistance(double currentLatitude, double currentLongitude) {
        if (startPointLatitude != 0 && startPointLongitude != 0){
            if (distance < trackLength){
                double theta = startPointLongitude - currentLongitude;
                distance = Math.sin(deg2rad(startPointLatitude)) * Math.sin(deg2rad(currentLatitude))
                        + Math.cos(deg2rad(startPointLatitude)) * Math.cos(deg2rad(currentLatitude))
                        * Math.cos(deg2rad(theta));
                distance = Math.acos(distance);
                distance = rad2deg(distance);
                distance = distance * 60 * 1.1515 * 1000;
                distanceMeter.setText(distance + " m");
                if (distance >= trackLength){
                    lapTime();
                    distance = 0;
                    distanceMeter.setText(distance + "m");
                }
            }else {
                distance = 0;
            }
            return distance;
        }
        return distance;
    }

    private void lapTime() {
        Toast.makeText(context, "LAP!", Toast.LENGTH_LONG).show();
        ListElementsArrayList.add(raceModeTimerTextView.getText().toString());
        adapter.notifyDataSetChanged();
    }

    private void setStartPoint(Location location) {
        startPointLatitude = location.getLatitude();
        startPointLongitude = location.getLongitude();
    }

    private double deg2rad(double deg){
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad){
        return (rad * 180.0 / Math.PI);
    }

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimeThread, 0);
    }

    private void stopTimer() {
        timeSwapBuff += timeInMs;
        handler.removeCallbacks(updateTimeThread);
        clearData();
    }

    private void clearData() {
        Toast.makeText(context, "RESET!", Toast.LENGTH_LONG).show();
        startTime = 0L;
        timeInMs = 0L;
        timeSwapBuff = 0L;
        updateTime = 0L;
        sec = 0;
        min = 0;
        ms = 0;
        distance = 0;
        raceModeTimerTextView.setText("00:00:000");
        raceModeSpeedometerTextView.setText("0");
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
}
