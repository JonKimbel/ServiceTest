package com.jonkimbel.servicetest.designoptions;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Optional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class ResultSaver {
    private final static String TAG = "ResultSaver";
    private final Context applicationContext;

    ResultSaver(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    void save(String result) {
        Log.d(TAG, "REKT Saving result = " + result);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(applicationContext.openFileOutput("result", Context.MODE_PRIVATE));
            outputStreamWriter.write(result);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }

    String load() {
        String fileString = "";

        try {
            InputStream inputStream = applicationContext.openFileInput("result");

            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder fileStringBuilder = new StringBuilder();
                String readLine;
                while ((readLine = bufferedReader.readLine()) != null) {
                    fileStringBuilder.append(readLine);
                }

                inputStream.close();
                fileString = fileStringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Cannot read file: " + e.toString());
            return null;
        }

        applicationContext.deleteFile("result");

        try {
            return fileString;
        } catch (NumberFormatException e) {
            Log.e(TAG, "NaN: " + fileString);
            return null;
        }
    }
}
