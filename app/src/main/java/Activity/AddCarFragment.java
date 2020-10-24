package Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.speedometer.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import Other.AutoData;


public class AddCarFragment extends Fragment{

    private static final String NEW_CAR = "NEW_CAR";
    private static final String USER_CAR = "USER_CAR";

    EditText carBrandEditText;
    EditText carModelEditText;
    EditText carPowerHP;

    Button confirmCarButton;

    AutoData autoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_car, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        carBrandEditText = (EditText) view.findViewById(R.id.carBrandEditText);
        carModelEditText = (EditText) view.findViewById(R.id.carModelEditText);
        carPowerHP = (EditText) view.findViewById(R.id.carPowerHP);
        confirmCarButton = (Button) view.findViewById(R.id.confirmCarButton);

        confirmCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserCar();
            }
        });
    }

    private void getUserCar() {
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
    }
}