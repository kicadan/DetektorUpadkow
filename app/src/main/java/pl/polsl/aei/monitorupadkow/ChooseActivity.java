package pl.polsl.aei.monitorupadkow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;



public class ChooseActivity extends AppCompatActivity {

    public enum MeasurementMode {
        COMPLEX,
        ECO,
        WEARABLE,
        PHONE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChooseActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    public void complex(View view){
        Intent intent = new Intent(ChooseActivity.this, StartActivity.class);
        intent.putExtra("MeasurementMode", MeasurementMode.COMPLEX.ordinal());
        // start the activity connect to the specified class
        startActivity(intent);
    }

    public void eco(View view){
        Intent intent = new Intent(ChooseActivity.this, StartActivity.class);
        intent.putExtra("MeasurementMode", MeasurementMode.ECO.ordinal());
        // start the activity connect to the specified class
        startActivity(intent);
    }

    public void wearable(View view){
        Intent intent = new Intent(ChooseActivity.this, StartActivity.class);
        intent.putExtra("MeasurementMode", MeasurementMode.WEARABLE.ordinal());
        // start the activity connect to the specified class
        startActivity(intent);
    }

    public void phone(View view){
        Intent intent = new Intent(ChooseActivity.this, StartActivity.class);
        intent.putExtra("MeasurementMode", MeasurementMode.PHONE.ordinal());
        // start the activity connect to the specified class
        startActivity(intent);
    }

    public void settings(View view){
        Intent intent = new Intent(ChooseActivity.this, SettingsActivity.class);
        // start the activity connect to the specified class
        startActivity(intent);
    }
}
