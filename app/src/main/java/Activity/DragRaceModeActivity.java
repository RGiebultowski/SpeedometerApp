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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedometer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import Other.DragRaceSaveTimesHandler;
import Other.LoggedAccHandler;

/*
 * Drag race mode. 2 funkcje. Bada czas przyspieszenia 0-100, 100-200, 0-200 w momencie kiedy to
 * aplikacja wykryje ruch
 *
 * Mierzenie danych w momencie gdy uzytkownik ustawi sobie "Counter"
 * */
public class DragRaceModeActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;

    private FloatingActionButton resetTimer;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    private TextView dragRaceSpeedometerTextView;
    private TextView dragRaceInfo;
    private TextView dragRaceTimerTextView;
    private ListView dragRaceTimesListView;

    private Spinner dragRaceChooseCar;

    private Button saveTimesButton;

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

    private String userName = "";
    private String from100to200time;
    private String from0to200time;
    private String selectedCar = "";

    private Context context = this;

    String[] ListElements = new String[]{"00:06:00","00:12:123","00:37:012"};
    List<String> ListElementsArrayList;
    ArrayAdapter<String> adapter;

    private ArrayAdapter chooseCarAdapter;
    private ArrayList<String> userCarsArrayList = new ArrayList<String>();

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
        dragRaceChooseCar = findViewById(R.id.dragRaceChooseCar);
        dragRaceInfo = findViewById(R.id.dragraceinfo);
        resetTimer = findViewById(R.id.resetTimer);
        saveTimesButton = findViewById(R.id.saveTimesButton);

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));
        chooseCarAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, userCarsArrayList);
        dragRaceChooseCar.setPrompt("Select your Car!");

        LoggedAccHandler lah = new LoggedAccHandler();
        userName = lah.getLoggedUserName();

        if (userName == null){
            userCarsArrayList.add("NO LOGGED USER");
            chooseCarAdapter.notifyDataSetChanged();
        }else {
            userCarsArrayList.clear();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference reference = databaseReference.child("Users").child(userName).child("Cars");
            Query checkUsersCar = reference.orderByChild("Cars");
            checkUsersCar.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String carBrand = ds.child("carBrand").getValue(String.class);
                        String carModel = ds.child("carModel").getValue(String.class);
                        String carHP = ds.child("carHP").getValue(String.class);
                        Log.d("TAG", carBrand + " / " + carModel + " / " + carHP);
                        String carData = carBrand + " " + carModel + " " + carHP;
                        userCarsArrayList.add(carData);
                    }
                    chooseCarAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        selectCar();

        adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                ListElementsArrayList
        );

        dragRaceTimesListView.setAdapter(adapter);
        dragRaceChooseCar.setAdapter(chooseCarAdapter);

        resetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });
        
        saveTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ListElementsArrayList.isEmpty()){
                    Toast.makeText(context, "Waiting for complete Run!.....", Toast.LENGTH_LONG).show();
                }else{
                    // TODO: 24.10.2020 zapisanie czasów próby do bazy danych i przekazanie ich do HomeFragment
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference("Users").child(userName).child("Drag");
                    String from0to100time = ListElementsArrayList.get(0);
                    String carData = selectedCar;
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                    String currentDate = format.format(new Date());
                    if (ListElementsArrayList.size() > 1){
                        from100to200time = ListElementsArrayList.get(1);
                        from0to200time = ListElementsArrayList.get(2);
                    }else {
                        from100to200time = "no data";
                        from0to200time = "no data";
                    }
                    DragRaceSaveTimesHandler dragRaceSaveTimesHandler = new DragRaceSaveTimesHandler(userName, carData, from0to100time, from0to200time, from100to200time, currentDate);
                    Random random = new Random();
                    int randomInt = random.nextInt(500000);
                    databaseReference.child(String.valueOf(randomInt)).setValue(dragRaceSaveTimesHandler);
                    Toast.makeText(context, "Times Saved!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void selectCar() {
        dragRaceChooseCar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCar = userCarsArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                from0to200();
                from100to200++;
            }
        }
    }

    private void from0to100SetTime() {
        ListElementsArrayList.add("0 - 100: " + dragRaceTimerTextView.getText().toString());
        adapter.notifyDataSetChanged();
    }

    private void from100to200SetTime() {
        String lastTime0to100 = ListElementsArrayList.get(ListElementsArrayList.size() - 1);
        String timeFrom0to200 = dragRaceTimerTextView.getText().toString();
        String[] parsingTime0to100 = lastTime0to100.split(":");
        String[] parsingTime0to200 = timeFrom0to200.split(":");

        parsingTime0to100[1] = parsingTime0to100[1].trim();
        int min100 = Integer.parseInt(parsingTime0to100[1]);
        int sec100 = Integer.parseInt(parsingTime0to100[2]);
        int ms100 = Integer.parseInt(parsingTime0to100[3]);

        int min200 = Integer.parseInt(parsingTime0to200[0]);
        int sec200 = Integer.parseInt(parsingTime0to200[1]);
        int ms200 = Integer.parseInt(parsingTime0to200[2]);

        String formattedMs100to200;
        if (ms100 > ms200) {
            int ms1 = ms100 - ms200;
            sec200 = sec200 - 1;
            int baseMs = 999;
            int ms100to200 = baseMs - ms1;
            if (ms100to200 <=9){
                formattedMs100to200 = "00" + ms100to200;
            }else if (ms100to200 <= 99){
                formattedMs100to200 = "0" + ms100to200;
            }else{
                formattedMs100to200 = String.valueOf(ms100to200);
            }
        } else {
            formattedMs100to200 = String.valueOf(ms200 - ms100);
        }

        int sec100to200;
        String formattedSec100to200;
        if (sec100 > sec200) {
            int sec1 = sec100 - sec200;
            min200 = min200 - 1;
            int baseSec = 60;
            sec100to200 = baseSec - sec1;
            if (sec100to200 <= 9){
                formattedSec100to200 = "0" + sec100to200;
            }else{
                formattedSec100to200 = String.valueOf(sec100to200);
            }
        } else {
            sec100to200 = sec200 - sec100;
            formattedSec100to200 = String.valueOf(sec100to200);
        }

        int min100to200;
        if (min100 > min200) {
            min100to200 = min100 - min200;
        } else {
            min100to200 = min200 - min100;
        }

        ListElementsArrayList.add("100 - 200: " + "0"+ min100to200 + ":" + formattedSec100to200 + ":" + formattedMs100to200);
        adapter.notifyDataSetChanged();
    }

    private void from0to200() {
        ListElementsArrayList.add("0 - 200: " + dragRaceTimerTextView.getText().toString());
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
