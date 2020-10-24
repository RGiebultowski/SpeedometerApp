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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private Button symulacja;

    private Handler handler = new Handler();

    private long startTime = 0L;
    private long timeInMs = 0L;
    private long updateTime = 0L;
    private long timeSwapBuff = 0L;

    private int sec;
    private int min;
    private int ms;
    private int from0to100 = 0;
    private int from100to200 = 0;

    private String symulacjaCzas1 = "0 - 100: " + "00:05:345";
    private String symulacjaCzas2 = "00:35:229";

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
        symulacja = findViewById(R.id.Symulacja);

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

        symulacja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //symulacja

                String[] parse0to100 = symulacjaCzas1.split(":");
                parse0to100[1] = parse0to100[1].trim();
                Integer mins = Integer.valueOf(parse0to100[1]);
                Integer sec = Integer.valueOf(parse0to100[2]);
                Integer ms = Integer.valueOf(parse0to100[3]);
                String[] parse0to200 = symulacjaCzas2.split(":");
                Integer mins2 = Integer.valueOf(parse0to200[0]);
                Integer sec2 = Integer.valueOf(parse0to200[1]);
                Integer ms2 = Integer.valueOf(parse0to200[2]);

                Integer diffMins = mins2 - mins;
                Integer diffSec = sec2 - sec;
                Integer diffMs = ms2 - ms;


                // TODO: 24.10.2020 obsluga - sec i poprawa ms zeby nie pokazywalo wartosci na -
                String from100to200 = "100 - 200: " + diffMins + " " + diffSec + " " + diffMs;
                ListElementsArrayList.add(from100to200);


                //String diff = time2 - time;
                //ListElementsArrayList.add("100 - 200: " + diff);

               /* SimpleDateFormat format = new SimpleDateFormat("mm:ss.SSS");
                Date d1 = null;
                try {
                    d1 = format.parse(symulacjaCzas1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date d2 = null;
                try {
                    d2 = format.parse(symulacjaCzas2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = d2.getTime() - d1.getTime();
                ListElementsArrayList.add("100 - 200: " + diff);*/
                adapter.notifyDataSetChanged();
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

            if (convertedSpeedToKmH >= 10 && convertedSpeedToKmH <= 15) {
                startTimer();
            } else if (convertedSpeedToKmH >= 7 && convertedSpeedToKmH <= 14) {
                stopTimer();
            }

            while (convertedSpeedToKmH >= 100 && convertedSpeedToKmH <= 105 && from0to100 < 1) {
                from0to100SetTime();
                from0to100++;
            }
            // TODO: 22.10.2020 mierzenie czasu od 100 do 200
            while (convertedSpeedToKmH >= 200 && convertedSpeedToKmH <= 205 && from100to200 < 1) {
                from100to200SetTime();
                from100to200++;
            }
        }
    }

    private void from100to200SetTime() {
        String lastTime0to100 = ListElementsArrayList.get(ListElementsArrayList.size() - 1);
        String timeFrom0to200 = dragRaceTimerTextView.getText().toString();
        String[] parsingTime0to100 = lastTime0to100.split(":");
        String[] parsingTime0to200 = timeFrom0to200.split(":");

        parsingTime0to100[1] = parsingTime0to100[1].trim();
        Integer min100 = Integer.valueOf(parsingTime0to100[1]);
        Integer sec100 = Integer.valueOf(parsingTime0to100[2]);
        Integer ms100 = Integer.valueOf(parsingTime0to100[3]);

        Integer min200 = Integer.valueOf(parsingTime0to200[0]);
        Integer sec200 = Integer.valueOf(parsingTime0to200[1]);
        Integer ms200 = Integer.valueOf(parsingTime0to200[2]);

        if (min100 > min200){

        }else{
            Integer diffMins = min200 - min100;
        }

        if (sec100 > sec200){

        }else{
            Integer diffSec = sec200 - sec100;
        }

        if (ms100 > ms200){
            
        }else {
            Integer diffMs = ms200 - ms100;
        }

        adapter.notifyDataSetChanged();
    }

    private void from0to100SetTime() {
        ListElementsArrayList.add("0 - 100: " + dragRaceTimerTextView.getText().toString());
        adapter.notifyDataSetChanged();
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
        from0to100 = 0;
        from100to200 = 0;
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
