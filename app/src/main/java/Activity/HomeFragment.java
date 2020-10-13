package Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.speedometer.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import Other.AutoData;


public class HomeFragment extends Fragment {

    // TODO: 11.10.2020 w momencie dodania nowego auta zrobic wyswietlenie jego w HomeFragment
    /*
     * Wcisniecie nowego auta na HomeFragment odpali okno danego auta, w nim będą wyspecjalizowane dane dotycznące konkretnego auta.
     * Tzn. Całkowity przejechany dystans na aplikacji
     * */
    private static final String NEW_CAR = "NEW_CAR";
    private static final String USER_CAR = "USER_CAR";


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
        carsListView = (ListView) view.findViewById(R.id.carsListView);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(USER_CAR, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NEW_CAR, null);
        Type type = new TypeToken<ArrayList<AutoData>>() {}.getType();
        ArrayList<AutoData> newUserCarList = gson.fromJson(json, type);
        if (newUserCarList != null){
            carList = newUserCarList;
            arrayAdapter.notifyDataSetChanged();
        }else {
            carList = new ArrayList<>();
        }
        arrayAdapter = new ArrayAdapter<AutoData>(getContext(), android.R.layout.simple_list_item_1, carList);
        carsListView.setAdapter(arrayAdapter);

    }

/*    public ArrayList<AutoData> getNewCar(ArrayList<AutoData> newCar) {
        if (newCar != null){
            carList = newCar;
        }else{
            carList = new ArrayList<>();
        }
        return null;
    }*/



        /*
        setClearPref(getContext());
    }

    // TODO: 12.10.2020 uaktualnic listview danymi z addcarfragment
    public void setClearPref(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(NEW_CAR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(NEW_CAR);
        editor.apply();
    }*/
}