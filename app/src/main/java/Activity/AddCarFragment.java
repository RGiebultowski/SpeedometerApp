package Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.speedometer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

import Other.AddCarToUserHandler;
import Other.AutoData;
import Other.LoggedAccHandler;


public class AddCarFragment extends Fragment{

    private static final String NEW_CAR = "NEW_CAR";
    private static final String USER_CAR = "USER_CAR";

    private String userName = null;

    EditText carBrandEditText;
    EditText carModelEditText;
    EditText carPowerHP;

    TextView carBrandTextView;
    TextView carModelTextView;
    TextView carPowerHPTextView;

    Button confirmCarButton;

    AutoData autoData;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_car, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        carBrandTextView = (TextView) view.findViewById(R.id.carBrandTextView);
        carModelTextView = (TextView) view.findViewById(R.id.carModelTextView);
        carPowerHPTextView = (TextView) view.findViewById(R.id.carPowerHPTextView);

        carBrandEditText = (EditText) view.findViewById(R.id.carBrandEditText);
        carModelEditText = (EditText) view.findViewById(R.id.carModelEditText);
        carPowerHP = (EditText) view.findViewById(R.id.carPowerHP);

        confirmCarButton = (Button) view.findViewById(R.id.confirmCarButton);

        LoggedAccHandler handler = new LoggedAccHandler();
        userName = handler.getLoggedUserName();

        confirmCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCarBrand() && validateCarModel() && validateCarPowerHP()){
                    getUserCar();
                }

            }
        });

        carBrandEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    validateCarBrand();
                }
            }
        });
        carModelEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    validateCarModel();
                }
            }
        });
        carPowerHP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    validateCarPowerHP();
                }
            }
        });
    }

    private void getUserCar() {
        if (userName == null){
            autoData = new AutoData(carBrandEditText.getText().toString(), carModelEditText.getText().toString(), carPowerHP.getText().toString());
            ArrayList<AutoData> userCarList = new ArrayList<>();
            autoData.toString();
            userCarList.add(autoData);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(USER_CAR, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(userCarList);
            editor.putString(NEW_CAR, json);
            editor.apply();
            Toast.makeText(getContext(), "Car added!", Toast.LENGTH_LONG).show();
        }else {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users").child(userName).child("Cars");
            String carBrand = carBrandEditText.getText().toString();
            String carModel = carModelEditText.getText().toString();
            String carHP = carPowerHP.getText().toString();
            AddCarToUserHandler handler = new AddCarToUserHandler(userName, carBrand, carModel, carHP);
            Random random = new Random();
            int randomInt = random.nextInt(5000);
            databaseReference.child(String.valueOf(randomInt)).setValue(handler);
            Toast.makeText(getContext(), "Car added!", Toast.LENGTH_LONG).show();
        }

    }

    private boolean validateCarBrand() {
        if (TextUtils.isEmpty(carBrandEditText.getText().toString())){
            carBrandTextView.setText("Car Brand must be added!");
            carBrandTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            carBrandTextView.setText("Car Brand");
            carBrandTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }

    private boolean validateCarPowerHP() {
        if (TextUtils.isEmpty(carPowerHP.getText().toString())){
            carPowerHPTextView.setText("Car Power HP must be added!");
            carPowerHPTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            carPowerHPTextView.setText("Car Power HP");
            carPowerHPTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }

    private boolean validateCarModel() {
        if (TextUtils.isEmpty(carModelEditText.getText().toString())){
            carModelTextView.setText("Car Model must be added!");
            carModelTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            carModelTextView.setText("Car Model");
            carModelTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }
}