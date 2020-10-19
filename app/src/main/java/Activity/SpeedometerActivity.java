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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedometer.R;



public class SpeedometerActivity extends AppCompatActivity implements LocationListener {

    private TextView speedometerTextView;
    private TextView km;

    private LocationManager locationManager;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedometer_activity);

        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            km.setVisibility(View.INVISIBLE);
            speedometerTextView.setTextSize(20f);
            speedometerTextView.setText(R.string.gpsFail);
        }
    }

    private void initView() {
        speedometerTextView = findViewById(R.id.speedometerTextView);
        km = findViewById(R.id.km);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkGPSConnection();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        permissionGranted();
    }

    @Override
    protected void onPause() {
        super.onPause();
        permissionGranted();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void checkGPSConnection() {
        permissionGranted();
        Toast.makeText(context, "Waiting for GPS connection...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO: 14.10.2020 formater do predkosci.
        if (location != null) {
            float speed = location.getSpeed();
            float convertedSpeedToKmH = speed * 3600 / 1000;
            int nCurrentSpeed = (int) convertedSpeedToKmH;
            String nStrCurrentSpeed = String.valueOf(nCurrentSpeed);
            speedometerTextView.setText(nStrCurrentSpeed);
        }
    }

    public void permissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
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
