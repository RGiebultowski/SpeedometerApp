package Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.speedometer.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import Other.AutoData;
import Other.LoggedAccHandler;


public class HomeFragment extends Fragment {

    // TODO: 11.10.2020 w momencie dodania nowego auta zrobic wyswietlenie jego w HomeFragment
    /*
     * Wcisniecie nowego auta na HomeFragment odpali okno danego auta, w nim będą wyspecjalizowane dane dotycznące konkretnego auta.
     * Tzn. Całkowity przejechany dystans na aplikacji
     * */
    private static final String NEW_CAR = "NEW_CAR";
    private static final String USER_CAR = "USER_CAR";

    private String userName = null;

    private ArrayList<String> carList = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
    private ListView carsListView;
    private TextView cars;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        carsListView = (ListView) view.findViewById(R.id.carsListView);
        cars = (TextView) view.findViewById(R.id.cars);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, carList);

        LoggedAccHandler lah = new LoggedAccHandler();
        userName = lah.getLoggedUserName();
        if (userName == null){
            addUserCar();
        }else {
            carList.clear();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference reference = databaseReference.child("Users").child(userName).child("Cars");
            Query checkUsersCar = reference.orderByChild("Cars");
            checkUsersCar.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    carList.add(String.valueOf(map.size()));*/
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String carBrand = ds.child("carBrand").getValue(String.class);
                        String carModel = ds.child("carModel").getValue(String.class);
                        String carHP = ds.child("carHP").getValue(String.class);
                        Log.d("TAG", carBrand + " / " + carModel + " / " + carHP);
                        String carData = carBrand + " " + carModel + " " + carHP;
                        carList.add(carData);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        carsListView.setAdapter(arrayAdapter);
    }

    private void addUserCar() {
       /* SharedPreferences sharedPreferences = getActivity().getSharedPreferences(USER_CAR, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NEW_CAR, null);
        Type type = new TypeToken<ArrayList<AutoData>>() {
        }.getType();
        ArrayList<AutoData> newUserCarList = gson.fromJson(json, type);
        if (newUserCarList != null) {
            AutoData newCar = newUserCarList.get(0);
            carListOffline.add(newCar);
            if (carListOffline != null){
                arrayAdapterOffline.notifyDataSetChanged();
            }
            newUserCarList.clear();
        } else {
            carListOffline = new ArrayList<>();
        }*/
    }
}