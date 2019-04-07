package com.jonkimbel.servicetest;

import android.os.Bundle;

import com.jonkimbel.servicetest.api.ActionCardViewModel;
import com.jonkimbel.servicetest.api.HasState;
import com.jonkimbel.servicetest.designoptions.SaveToDiskApproach;
import com.jonkimbel.servicetest.designoptions.BoundServiceApproach;
import com.jonkimbel.servicetest.designoptions.SimpleCallbackApproach;
import com.jonkimbel.servicetest.help.TurnOffDontKeepActivities;
import com.jonkimbel.servicetest.help.TurnOnDontKeepActivities;
import com.jonkimbel.servicetest.help.TurnOnDeveloperSettings;
import com.jonkimbel.servicetest.help.Tutorial;
import com.jonkimbel.servicetest.ui.ActionCardListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    /**
     * Keeps track of objects that need to save their state when the activity dies. The values are
     * not used, we only use a WeakHashMap for its weak properties.
     */
    private Map<HasState, Boolean> statefulObjects = new WeakHashMap<>();

    private ActionCardListAdapter listAdapter;

    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Draw layout.
        setContentView(R.layout.activity_main);

        // Create behaviors to populate the recycler view with.
        List<ActionCardViewModel> viewModels = new ArrayList<>();
        viewModels.add(new Tutorial());
        viewModels.add(SimpleCallbackApproach.newInstance(getApplicationContext(), statefulObjects, savedInstanceState));
        viewModels.add(SaveToDiskApproach.newInstance(getApplicationContext(), statefulObjects, savedInstanceState));
        viewModels.add(BoundServiceApproach.newInstance(getApplicationContext(), statefulObjects, savedInstanceState));

        // Set up recycler view.
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ActionCardListAdapter(viewModels);
        recyclerView.setAdapter(listAdapter);

        // Improves recycler view performance, changes in content do not change the layout size.
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        addCardsThatDependOnSettings();

        started = true;
        for (HasState hasState : statefulObjects.keySet()) {
            hasState.onStart();
        }
    }

    private void addCardsThatDependOnSettings() {
        // Appears at the top of the UI, asks the user to turn on developer settings.
        if (TurnOnDeveloperSettings.shouldShow(this)) {
            if (listAdapter.doesNotContain(TurnOnDeveloperSettings.class)) {
                listAdapter.addAtStart(new TurnOnDeveloperSettings(this));
            }
        } else {
            listAdapter.removeAll(TurnOnDeveloperSettings.class);
        }

        // Appears at the top of the UI, asks the user to turn on "Don't keep activities"
        if (TurnOnDontKeepActivities.shouldShow(this)) {
            if (listAdapter.doesNotContain(TurnOnDontKeepActivities.class)) {
                listAdapter.addAtStart(new TurnOnDontKeepActivities(this));
            }
        } else {
            listAdapter.removeAll(TurnOnDontKeepActivities.class);
        }

        // Appears at the bottom of the UI, reminds the user to turn off "Don't keep activities"
        // when they're done.
        if (TurnOffDontKeepActivities.shouldShow(this)) {
            if (listAdapter.doesNotContain(TurnOffDontKeepActivities.class)) {
                listAdapter.addAtEnd(new TurnOffDontKeepActivities(this));
            }
        } else {
            listAdapter.removeAll(TurnOffDontKeepActivities.class);
        }
    }

    @Override
    protected void onStop() {
        started = false;
        for (HasState hasState : statefulObjects.keySet()) {
            hasState.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        for (HasState hasState : statefulObjects.keySet()) {
            hasState.onSaveInstanceState(bundle);
        }
    }
}
