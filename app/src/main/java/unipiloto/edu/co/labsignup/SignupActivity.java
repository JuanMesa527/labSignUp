package unipiloto.edu.co.labsignup;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

public class SignupActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    TextView lat, lon;
    Button obtener;
    EditText editTextDate;

    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        lat = findViewById(R.id.edtlatitud);
        lon = findViewById(R.id.edtlongitud);
        editTextDate = findViewById(R.id.TextDate);

        obtener = findViewById(R.id.button);
    }

    public void ObtenerCoordendasActual(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SignupActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        } else {
            getCoordenada();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCoordenada();
            } else {
                Toast.makeText(this, "Permiso Denegado ..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCoordenada() {
        try {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(SignupActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitud = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        lat.setText(String.valueOf(latitud));
                        lon.setText(String.valueOf(longitude));
                    }
                }

            }, Looper.myLooper());
        }catch (Exception ex){
            System.out.println("Error es :" + ex);
        }
    }

    public void openDatePicker(View view) {
        Date date = new Date();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                int currentYear = date.getYear() + 1900;
                int age = currentYear - year;
                if (age < 18 || (age == 18 && (month > date.getMonth() || (month == date.getMonth() && day > date.getDate())))) {
                    Toast.makeText(SignupActivity.this, "Debes ser mayor de 18 años", Toast.LENGTH_SHORT).show();
                } else {
                    editTextDate.setText(String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                }
            }
        }, (date.getYear() + 1900), (date.getMonth()), (date.getDate()));

        datePickerDialog.show();
    }
}