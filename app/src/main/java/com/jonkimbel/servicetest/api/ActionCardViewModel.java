package com.jonkimbel.servicetest.api;

public interface ActionCardViewModel {
    int getTitleText();

    int getDescriptionText();

    default boolean isButtonVisible() {
        return true;
    }

    int getButtonText();

    boolean isButtonEnabled();

    int getButtonIcon();

    default boolean isSpecialCard() {
        return false;
    }

    void onClick();

    default void setDataChangedCallback(Runnable callback) {
    }
}
