package com.jonkimbel.servicetest.help;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.ActionCardViewModel;

public class Tutorial implements ActionCardViewModel {
    @Override
    public int getTitleText() {
        return R.string.tutorialTitle;
    }

    @Override
    public int getDescriptionText() {
        return R.string.tutorialDescription;
    }

    @Override
    public boolean isButtonVisible() {
        return false;
    }

    @Override
    public int getButtonText() {
        return R.string.tutorialButtonText;
    }

    @Override
    public boolean isButtonEnabled() {
        return false;
    }

    @Override
    public int getButtonIcon() {
        return 0;
    }

    @Override
    public void onClick() {
    }
}