package pl.polsl.aei.monitorupadkow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    String filename = "settings.txt";
    String phoneNumber;
    EditText phoneNumberEdit;
    //phoneNumberEdit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        phoneNumberEdit = (EditText)findViewById(R.id.phoneNumberEdit);
    }

    public void save(View view){
        File path = getApplicationContext().getExternalFilesDir(null);
        File settingsFile = new File(path, filename);
        try {
            FileOutputStream stream = new FileOutputStream(settingsFile);
            stream.write(generateSettingsFile().getBytes());
            stream.flush();
            stream.close();
        } catch(IOException e){
            System.out.println(e.toString());
        }
    }

    public void back(View view){
        Intent intent = new Intent(SettingsActivity.this, ChooseActivity.class);
        // start the activity connect to the specified class
        startActivity(intent);
    }

    public String generateSettingsFile(){
        return "{\n\t\"PhoneNumber\" : \"" + phoneNumberEdit.getText().toString() + "\"\n}";
    }
}
