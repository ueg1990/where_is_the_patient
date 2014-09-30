package com.estimote.examples.demos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.estimote.sdk.Beacon;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class RegisterActivity extends Activity {

    private Beacon beacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
        if (beacon == null) {
            Toast.makeText(this, "Beacon not found in intent extras", Toast.LENGTH_LONG).show();
            finish();
        }

        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText first_name = (EditText) findViewById(R.id.first_name);
                EditText last_name = (EditText) findViewById(R.id.last_name);
                try {
                    FileOutputStream fout = openFileOutput("patients_register.txt", MODE_APPEND);
                    OutputStreamWriter out = new OutputStreamWriter(fout);
                    out.write(first_name.getText().toString() + ";" + last_name.getText().toString() + ";" + beacon.getMacAddress() + "\n");
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RegisterActivity.this.finish();

            }
        });

    }


}
