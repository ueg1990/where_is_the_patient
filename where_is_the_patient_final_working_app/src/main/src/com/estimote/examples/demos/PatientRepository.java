package com.estimote.examples.demos;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.*;

/**
 * Created by wei on 27/09/14.
 */
public class PatientRepository {

    String findPatient(Context context, String beaconId) {

        FileInputStream finput;
        try {
            String line;
            finput = context.openFileInput("patients_register.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(finput));
            while ((line = in.readLine()) != null) {
                String[] word = line.split(";");
                String first_name = word[0];
                String last_name = word[1];
                String patient_id = word[2];
            	
                if (patient_id.equals(beaconId)) {
                    return first_name + " " + last_name;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new Resources.NotFoundException("Unable to find patient");
    }
}
