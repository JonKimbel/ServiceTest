package com.jonkimbel.servicetest.api;

import android.os.Bundle;

/**
 * Created by Jon on 3/31/2019.
 */

public interface HasState {
    void onStart();

    void onSaveInstanceState(Bundle bundle);

    void onStop();
}
