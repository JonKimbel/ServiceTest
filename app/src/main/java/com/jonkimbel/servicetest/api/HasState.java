package com.jonkimbel.servicetest.api;

import android.os.Bundle;

public interface HasState {
    void onStart();

    void onSaveInstanceState(Bundle bundle);

    void onStop();
}
