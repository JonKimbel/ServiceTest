package com.jonkimbel.servicetest.api;

public interface ActionCardViewModel {
    String getTitleText();

    String getDescriptionText();

    boolean getCheckMarkViewVisibility();

    void onClick();

    void setDataChangedCallback(Runnable callback);

    boolean getWaitingIconViewVisibility();
}
