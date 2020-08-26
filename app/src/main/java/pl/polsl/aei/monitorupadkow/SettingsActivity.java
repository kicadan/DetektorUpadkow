package pl.polsl.aei.monitorupadkow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    static String filename = "settings.txt";
    EditText phoneNumberEdit;
    //phoneNumberEdit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        phoneNumberEdit = (EditText)findViewById(R.id.phoneNumberEdit);
        phoneNumberEdit.setText(getPhoneNumber(getApplicationContext()));
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
        finish();
    }

    public String generateSettingsFile(){
        return phoneNumberEdit.getText().toString().trim();
    }

    public static String getPhoneNumber(Context context){
        File path = context.getExternalFilesDir(null);
        File settingsFile = new File(path, filename);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(settingsFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch(IOException e){
            System.out.println(e.toString());
        }
        return text.toString();
    }
}
