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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedometer.R;

import java.util.Formatter;
import java.util.Locale;

public class SpeedometerActivity extends AppCompatActivity implements LocationListener {

    private TextView speedometerTextView;

    private LocationManager locationManager;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedometer_activity);
        // TODO: 10.10.2020 załadować widok po zezwoleniu na gps,zfreezować aplikacje?
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        } else {
            checkGPSConnection();
        }

    }

    private void initView() {
        speedometerTextView = findViewById(R.id.speedometerTextView);
    }

/*    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100L, 2.0f, (LocationListener) this);
    }*/

    @SuppressLint("MissingPermission")
    private void checkGPSConnection() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        }
        Toast.makeText(context, "Waiting for GPS connection...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location !=null){
            float speed = location.getSpeed();
            float convertedSpeedToKmH = speed * 3600 / 1000;
            Formatter formatter = new Formatter(new StringBuilder());
            formatter.format(Locale.US, "%2.0f", convertedSpeedToKmH);
            String strCurrentSpeed = formatter.toString();
            strCurrentSpeed = strCurrentSpeed.replace(" ", "0");
            speedometerTextView.setText(strCurrentSpeed + " km/h");
        }
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
