package com.jonkimbel.servicetest.ui;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.ActionCardViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ActionCardListAdapter extends RecyclerView.Adapter<ActionCardListAdapter.ActionCardViewHolder> {
    private List<ActionCardViewModel> viewModels;

    public ActionCardListAdapter(List<ActionCardViewModel> viewModels) {
        this.viewModels = viewModels;
    }

    @Override
    @NonNull
    public ActionCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collapsible_card_with_button, parent, false);
        return new ActionCardViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionCardViewHolder view, int position) {
        ActionCardViewModel viewModel = viewModels.get(position);

        view.setTitleText(viewModel.getTitleText());
        view.setDescriptionText(viewModel.getDescriptionText());
        view.setButtonVisible(viewModel.isButtonVisible());
        if (viewModel.isButtonVisible()) {
            view.setButtonText(viewModel.getButtonText());
            view.setButtonEnabled(viewModel.isButtonEnabled());
            view.setButtonIcon(viewModel.getButtonIcon());
        }
        view.setOnClick(v -> viewModel.onClick());
        view.setIsSpecialCard(viewModel.isSpecialCard());

        viewModel.setDataChangedCallback(() -> notifyItemChanged(position));
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public boolean doesNotContain(Class<? extends ActionCardViewModel> clazz) {
        for (ActionCardViewModel viewModel : viewModels) {
            if (viewModel.getClass() == clazz) {
                return false;
            }
        }
        return true;
    }

    public void removeAll(Class<? extends ActionCardViewModel> clazz) {
        for (int i = viewModels.size() - 1; i >= 0; i--) {
            if (viewModels.get(i).getClass() == clazz) {
                viewModels.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void addAtStart(ActionCardViewModel viewModel) {
        viewModels.add(0, viewModel);
        notifyItemInserted(0);
    }

    public void addAtEnd(ActionCardViewModel viewModel) {
        viewModels.add(viewModel);
        notifyItemInserted(viewModels.size() - 1);
    }

    static class ActionCardViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView titleView;
        private final TextView descriptionView;
        private final Chip buttonView;

        ActionCardViewHolder(MaterialCardView cardView) {
            super(cardView);

            this.cardView = cardView;
            titleView = cardView.findViewById(R.id.title_text);
            descriptionView = cardView.findViewById(R.id.description_text);
            buttonView = cardView.findViewById(R.id.button);
        }

        private static ColorStateList color(Resources res, int colorId) {
            return ColorStateList.valueOf(res.getColor(colorId));
        }

        void setTitleText(int textId) {
            titleView.setText(Formatter.formatString(titleView.getResources(), textId));
        }

        void setDescriptionText(int textId) {
            descriptionView.setText(Formatter.formatString(descriptionView.getResources(), textId));
        }

        void setButtonVisible(boolean buttonVisible) {
            buttonView.setVisibility(buttonVisible ? View.VISIBLE : View.GONE);
        }

        void setButtonText(int textId) {
            buttonView.setText(Formatter.formatString(buttonView.getResources(), textId));
        }

        void setButtonEnabled(boolean buttonEnabled) {
            buttonView.setEnabled(buttonEnabled);
        }

        void setOnClick(View.OnClickListener listener) {
            buttonView.setOnClickListener(listener);
        }

        void setButtonIcon(int buttonIcon) {
            buttonView.setChipIcon(buttonView.getResources().getDrawable(buttonIcon));
        }

        void setIsSpecialCard(boolean specialCard) {
            Resources res = cardView.getResources();
            if (specialCard) {
                TextViewCompat.setTextAppearance(titleView, R.style.ActionCardText_Special_Title);
                TextViewCompat.setTextAppearance(descriptionView, R.style.ActionCardText_Special);
                setColors(
                        /* foregroundColor = */ color(res, R.color.colorBackground),
                        /* backgroundColor = */ color(res, R.color.colorPrimary));
            } else {
                TextViewCompat.setTextAppearance(titleView, R.style.ActionCardText);
                TextViewCompat.setTextAppearance(descriptionView, R.style.ActionCardText);
                setColors(
                        /* foregroundColor = */ color(res, R.color.colorPrimary),
                        /* backgroundColor = */ color(res, R.color.colorBackground));
            }
        }

        private void setColors(ColorStateList foregroundColor, ColorStateList backgroundColor) {
            buttonView.setChipBackgroundColor(foregroundColor);

            buttonView.setTextColor(backgroundColor);
            buttonView.setChipIconTint(backgroundColor);
            cardView.setCardBackgroundColor(backgroundColor);
        }
    }
}