package com.jonkimbel.servicetest;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Optional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ResultSaver {
    private final static String TAG = "ResultSaver";
    private final Context applicationContext;

    public ResultSaver(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void save(int result) {
        Log.d(TAG, "REKT Saving result = " + result);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(applicationContext.openFileOutput("result", Context.MODE_PRIVATE));
            outputStreamWriter.write(Integer.toString(result));
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }

    public Optional<Integer> load() {
        String fileString = "";

        try {
            InputStream inputStream = applicationContext.openFileInput("result");

            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder fileStringBuilder = new StringBuilder();
                String readLine = "";
                while ((readLine = bufferedReader.readLine()) != null) {
                    fileStringBuilder.append(readLine);
                }

                inputStream.close();
                fileString = fileStringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            return Optional.absent();
        } catch (IOException e) {
            Log.e(TAG, "Cannot read file: " + e.toString());
            return Optional.absent();
        }

        applicationContext.deleteFile("result");

        try {
            return Optional.of(Integer.parseInt(fileString));
        } catch (NumberFormatException e) {
            Log.e(TAG, "NaN: " + fileString);
            return Optional.absent();
        }
    }
}
