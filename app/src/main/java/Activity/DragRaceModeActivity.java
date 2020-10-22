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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedometer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Drag race mode. 2 funkcje. Bada czas przyspieszenia 0-100, 100-200, 0-200 w momencie kiedy to
 * aplikacja wykryje ruch
 *
 * Mierzenie danych w momencie gdy uzytkownik ustawi sobie "Counter"
 * */
public class DragRaceModeActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;

    private FloatingActionButton resetTimer;

    private TextView dragRaceSpeedometerTextView;
    private TextView dragRaceInfo;
    private TextView dragRaceTimerTextView;
    private ListView dragRaceTimesListView;

    private Handler handler = new Handler();

    private long startTime = 0L;
    private long timeInMs = 0L;
    private long updateTime = 0L;
    private long timeSwapBuff = 0L;

    private int sec;
    private int min;
    private int ms;
    private int cunter0to100 = 0;

    private Context context = this;

    String[] ListElements = new String[]{ };
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
            dragRaceTimerTextView.setText("" + String.format("%02d", min) + ":" + String.format("%02d", sec) + ":" + String.format("%03d", ms));
            handler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragrace_activity);

        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            dragRaceInfo.setTextSize(20f);
            dragRaceInfo.setText(R.string.gpsFail);
        }
    }

    private void initView() {
        dragRaceSpeedometerTextView = findViewById(R.id.dragRaceSpeedometerTextView);
        dragRaceTimesListView = findViewById(R.id.dragRaceTimesListView);
        dragRaceTimerTextView = findViewById(R.id.dragRaceTimerTextView);
        dragRaceInfo = findViewById(R.id.dragraceinfo);
        resetTimer = findViewById(R.id.resetTimer);

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));

        adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                ListElementsArrayList
        );

        dragRaceTimesListView.setAdapter(adapter);

        resetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkGPSConnection();
        }
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

    @SuppressLint("MissingPermission")
    private void checkGPSConnection() {
        permissionGranted();
        Toast.makeText(context, "Waiting for GPS connection...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            float speed = location.getSpeed();
            float convertedSpeedToKmH = speed * 3600 / 1000;
            int nCurrentSpeed = (int) convertedSpeedToKmH;
            String nStrCurrentSpeed = String.valueOf(nCurrentSpeed);
            dragRaceSpeedometerTextView.setText(nStrCurrentSpeed);

            if (convertedSpeedToKmH >= 15){
                startTimer();
            }else if (convertedSpeedToKmH >= 7 && convertedSpeedToKmH <= 14){
                stopTimer();
            }

            while(convertedSpeedToKmH == 100 && cunter0to100 > 1){
                ListElementsArrayList.add(dragRaceTimerTextView.getText().toString());
                adapter.notifyDataSetChanged();
                cunter0to100 ++;
            }
            // TODO: 22.10.2020 mierzenie czasu od 100 do 200
        }
    }

    public void permissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        }
    }

    public void startTimer() {
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
        cunter0to100 = 0;
        startTime = 0L;
        timeInMs = 0L;
        timeSwapBuff = 0L;
        updateTime = 0L;
        sec = 0;
        min = 0;
        ms = 0;
        dragRaceTimerTextView.setText("00:00:000");
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
