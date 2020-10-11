package Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.speedometer.R;

import java.util.ArrayList;

import Other.AutoData;
import Other.Utils;

public class HomeFragment extends Fragment {

    // TODO: 11.10.2020 w momencie dodania nowego auta zrobic wyswietlenie jego w HomeFragment
    /*
    * Wcisniecie nowego auta na HomeFragment odpali okno danego auta, w nim będą wyspecjalizowane dane dotycznące konkretnego auta.
    * Tzn. Całkowity przejechany dystans na aplikacji
    * */
    private static final String NEW_CAR = "NEW_CAR";

    private AutoData autoData;
    private ArrayList<AutoData> carList;
    private ArrayAdapter<AutoData> arrayAdapter;
    private ListView carsListView;

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
        // TODO: 11.10.2020 obsługa nullpointera
        carsListView = (ListView) view.findViewById(R.id.carsListView);
        Bundle bundle = getArguments();
        String newAutoData = bundle.getString(NEW_CAR);
        autoData = Utils.getGsonParser().fromJson(newAutoData, AutoData.class);
    }
}