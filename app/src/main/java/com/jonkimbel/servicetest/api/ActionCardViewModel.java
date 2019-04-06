package com.jonkimbel.servicetest.api;

public interface ActionCardViewModel {
    int getTitleText();

    int getDescriptionText();

    int getButtonText();

    boolean isButtonEnabled();

    int getButtonIcon();

    boolean isSpecialCard();

    void onClick();

    void setDataChangedCallback(Runnable callback);
}
