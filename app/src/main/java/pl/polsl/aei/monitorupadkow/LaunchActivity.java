package pl.polsl.aei.monitorupadkow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LaunchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, ChooseActivity.class);

                // start the activity connect to the specified class
                startActivity(intent);
            }
        }, 3000);
    }
}
