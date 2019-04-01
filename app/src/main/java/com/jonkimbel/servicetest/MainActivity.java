package com.jonkimbel.servicetest;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.collect.ImmutableList;

import java.util.Map;
import java.util.WeakHashMap;

public final class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    /**
     * Keeps track of objects that need to save their state when the activity dies. The values are
     * not used, we only use a WeakHashMap for its weak properties.
     */
    private Map<HasState, Boolean> statefulObjects = new WeakHashMap<>();

    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Draw layout.
        setContentView(R.layout.activity_main);

        // Create behaviors to populate the recycler view with.
        ImmutableList.Builder<ActionCardViewModel> viewModels = new ImmutableList.Builder<>();
        viewModels.add(SaveToDisk.newInstance(getApplicationContext(), statefulObjects, savedInstanceState));

        // Set up recycler view.
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ActionCardListAdapter(viewModels.build()));

        // Improves recycler view performance, changes in content do not change the layout size.
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        started = true;
        for (HasState hasState : statefulObjects.keySet()) {
            hasState.onStart();
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
        for (HasState hasState : statefulObjects.keySet()) {
            hasState.onSaveInstanceState(bundle);
        }
    }
}
