package Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.speedometer.R;
import com.google.gson.Gson;

import Other.AutoData;
import Other.Utils;

public class AddCarFragment extends Fragment{

    public static final String NEW_CAR = "NEW_CAR";

    EditText carBrandEditText;
    EditText carModelEditText;
    EditText carPowerHP;

    Button confirmCarButton;

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
                AutoData autoData = new AutoData(carBrandEditText.getText().toString(), carModelEditText.getText().toString(), Integer.valueOf(carPowerHP.getText().toString()));
                Bundle bundle = new Bundle();
                String newAutoData = Utils.getGsonParser().toJson(autoData);
                bundle.putString(NEW_CAR, newAutoData);
                Toast.makeText(getContext(), "Car added!", Toast.LENGTH_LONG).show();
            }
        });
    }
}