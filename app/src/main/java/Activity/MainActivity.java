package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.speedometer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;


    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        } else {
            Toast.makeText(context, "Waiting for GPS connection...", Toast.LENGTH_LONG).show();
        }
    }


    private void initView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_draw_open, R.string.nav_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setSelectedItemId(R.id.Home);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = fragmentSwitcher(menuItem);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                return true;
            }
        });
    }

    private Fragment fragmentSwitcher(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.AddCar:
                fragment = new AddCarFragment();
                break;
            case R.id.Home:
                fragment = new HomeFragment();
                break;
            case R.id.Times:
                fragment = new BestTimesFragment();
                break;
        }
        return fragment;
    }

    //Zabezpieczenie przed wyłączeniem aplikacji w momencie gdy boczne menu jest otwarte.
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // TODO: 23.10.2020 system logowania
        Intent intent;
        switch (menuItem.getItemId()){
            case R.id.speedometer:
                intent = new Intent(context, SpeedometerActivity.class);
                startActivity(intent);
                break;
            case R.id.dragRaceMode:
                intent = new Intent(context, DragRaceModeActivity.class);
                startActivity(intent);
                break;
            case R.id.raceMode:
                intent = new Intent(context, RaceModeSetLapLengthActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}