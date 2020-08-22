package pl.polsl.aei.monitorupadkow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
