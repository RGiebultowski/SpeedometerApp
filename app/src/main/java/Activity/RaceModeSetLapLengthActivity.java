package Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedometer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Other.LoggedAccHandler;

public class RaceModeSetLapLengthActivity extends AppCompatActivity {

    private EditText trackLengthEditText;
    private TextView trackLengthTextView;

    private Spinner trackNameSpinner;
    private Spinner chooseCarSpinner;

    private Button startRaceModeButton;

    private static final String TRACK_LENGTH = "TRACK_LENGTH";
    private static final String CHOSEN_CAR = "CHOSEN_CAR";
    private static final String CHOSEN_TRACK = "CHOSEN_TRACK";
    private static final String NO_LOGGED_USER = "NO_LOGGED_USER";

    private String[] tracks = new String[]{"Radom - Jastrząb", "Poznań - Tor główny", "Poznań - Tor Kartingowy", "Kraków",
     "Łódź", "Gdańsk - Pszczółki", "Wrocław - Krzywa", "Lublin - Ułęż", "Białystok", "Toruń", "Warszawa - Modlin", "Warszawa - Słomczyn",
     "Koszalin", "Bednary", "Borsk", "Tor Silesia Ring Karting", "Kielce", "Inny"};
    private String[] tracksLengths = new String[]{"2400", "4100", "1500", "1050", "1050", "1200", "2000", "2500", "1050", "1050", "1200",
     "1200", "1050", "2500", "1200", "3600", "2500"};
    private ArrayList<String> userCarsArrayList = new ArrayList<String>();
    private List<String> tracksLengthsArrayList;
    private List<String> tracksArrayList;

    private String userName = "";
    private String selectedCar = "";
    private String selectedTrack = "";

    private ArrayAdapter tracksAdapter;
    private ArrayAdapter chooseCarAdapter;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.racemodelaplenght_activity);
        initView();
    }

    private void initView() {
        trackLengthEditText = findViewById(R.id.trackLengthEditText);
        trackLengthTextView = findViewById(R.id.trackLengthTextView);

        trackNameSpinner = findViewById(R.id.trackNameSpinner);
        chooseCarSpinner = findViewById(R.id.chooseCarSpinner);

        startRaceModeButton = findViewById(R.id.startRaceModeButton);

        trackLengthEditText.setFocusable(false);



        tracksArrayList = new ArrayList<String>(Arrays.asList(tracks));

        tracksAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, tracksArrayList);
        trackNameSpinner.setAdapter(tracksAdapter);

        chooseCarSpinner.setPrompt("Select your Car!");
        chooseCarAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, userCarsArrayList);
        chooseCarSpinner.setAdapter(chooseCarAdapter);

        LoggedAccHandler lah = new LoggedAccHandler();
        userName = lah.getLoggedUserName();

        userCarsfromDB();

        selectCar();

        tracksLengthsArrayList = new ArrayList<String>(Arrays.asList(tracksLengths));


        trackNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTrack = tracksArrayList.get(position);
                switch (position){
                    case 0:
                        //Radom
                        trackLengthEditText.setText(tracksLengthsArrayList.get(0));
                        break;
                    case 1:
                        //poznań główny
                        trackLengthEditText.setText(tracksLengthsArrayList.get(1));
                        break;
                    case 2:
                        //poznań karting
                        trackLengthEditText.setText(tracksLengthsArrayList.get(2));
                        break;
                    case 3:
                        //kraków
                        trackLengthEditText.setText(tracksLengthsArrayList.get(3));
                        break;
                    case 4:
                        //łódź
                        trackLengthEditText.setText(tracksLengthsArrayList.get(4));
                        break;
                    case 5:
                        //Gdańsk
                        trackLengthEditText.setText(tracksLengthsArrayList.get(5));
                        break;
                    case 6:
                        //Wrocław
                        trackLengthEditText.setText(tracksLengthsArrayList.get(6));
                        break;
                    case 7:
                        //Lublin
                        trackLengthEditText.setText(tracksLengthsArrayList.get(7));
                        break;
                    case 8:
                        //Białystok
                        trackLengthEditText.setText(tracksLengthsArrayList.get(8));
                        break;
                    case 9:
                        //Toruń
                        trackLengthEditText.setText(tracksLengthsArrayList.get(9));
                        break;
                    case 10:
                        //Warszawa - modlin
                        trackLengthEditText.setText(tracksLengthsArrayList.get(10));
                        break;
                    case 11:
                        //Warszawa - Słomczyn
                        trackLengthEditText.setText(tracksLengthsArrayList.get(11));
                        break;
                    case 12:
                        //Koszalin
                        trackLengthEditText.setText(tracksLengthsArrayList.get(12));
                        break;
                    case 13:
                        //Bednary
                        trackLengthEditText.setText(tracksLengthsArrayList.get(13));
                        break;
                    case 14:
                        //borsk
                        trackLengthEditText.setText(tracksLengthsArrayList.get(14));
                        break;
                    case 15:
                        //Tor Silesia Ring Karting
                        trackLengthEditText.setText(tracksLengthsArrayList.get(15));
                        break;
                    case 16:
                        //kielce
                        trackLengthEditText.setText(tracksLengthsArrayList.get(16));
                        break;
                    case 17:
                        //inny
                        trackLengthEditText.setFocusableInTouchMode(true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        trackLengthEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    validateTrackLength();
                }
            }
        });

        startRaceModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateTrackLength()){
                    Intent intent = new Intent(context, RaceModeActivity.class);
                    intent.putExtra(TRACK_LENGTH, trackLengthEditText.getText().toString());
                    if (selectedTrack.isEmpty()){
                        intent.putExtra(CHOSEN_TRACK, tracksArrayList.get(0));
                    }else{
                        intent.putExtra(CHOSEN_TRACK, selectedTrack);
                    }
                    if (selectedCar.isEmpty()){
                        intent.putExtra(CHOSEN_CAR, userCarsArrayList.get(0));
                    }else{
                        intent.putExtra(CHOSEN_CAR, selectedCar);
                    }
                    startActivity(intent);
                }
            }
        });
    }

    private void selectCar() {
        chooseCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCar = userCarsArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void userCarsfromDB() {
        if (userName == null){
            userCarsArrayList.add(NO_LOGGED_USER);
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
    }

    private boolean validateTrackLength() {
        if (TextUtils.isEmpty(trackLengthEditText.getText().toString())){
            trackLengthTextView.setText("Track Length must be added!");
            trackLengthTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            trackLengthTextView.setText("Track Lenght in meters");
            trackLengthTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }
}
