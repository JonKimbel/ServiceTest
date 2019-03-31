package com.jonkimbel.servicetest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.common.base.Optional;

import java.util.Map;
import java.util.WeakHashMap;

public final class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    /**
     * Keeps track of objects that need to save their state when the activity dies. The values are
     * not used, we only use a WeakHashMap for its weak properties.
     */
    private Map<HasStateToSave, Boolean> statefulObjects = new WeakHashMap<>();

    // Non-null after onCreate().
    private Timer timer;
    private ResultSaver resultSaver;

    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Draw layout.
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Instantiate fields.
        timer = Timer.newInstance((TextView) findViewById(R.id.timer_text), savedInstanceState, statefulObjects);
        resultSaver = new ResultSaver(getApplicationContext());

        // Set listeners.
        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(v -> onButtonClick());
    }

    @Override
    protected void onStart() {
        super.onStart();
        started = true;
        Optional<Integer> result = resultSaver.load();
        if (result.isPresent()) {
            processResult(result.get());
        }
    }

    @Override
    protected void onStop() {
        started = false;
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        for (HasStateToSave hasStateToSave : statefulObjects.keySet()) {
            hasStateToSave.onSaveInstanceState(bundle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onButtonClick() {
        timer.start();
        new SlowOperation(result -> {
            if (started) {
                processResult(result);
            } else {
                resultSaver.save(result);
            }
        }).start();
    }

    private void processResult(int result) {
        Log.d(TAG, "REKT Operation completed, result = " + result);
        Snackbar.make((FloatingActionButton) findViewById(R.id.fab), "Operation completed, result = " + result, Snackbar.LENGTH_LONG).show();
        timer.stop();
    }
}
