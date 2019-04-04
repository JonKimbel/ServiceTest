package com.jonkimbel.servicetest.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.ActionCardViewModel;

import java.util.List;

import androidx.annotation.NonNull;
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
        view.setOnClick(v -> viewModel.onClick());
        view.setCheckMarkViewVisibility(viewModel.getCheckMarkViewVisibility());
        view.setWaitingIconViewVisibility(viewModel.getWaitingIconViewVisibility());

        viewModel.setDataChangedCallback(() -> notifyItemChanged(position));
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    static class ActionCardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView timerView;
        private TextView descriptionView;
        private View buttonView;
        private View checkMarkView;
        private View waitingIconView;

        ActionCardViewHolder(MaterialCardView cardView) {
            super(cardView);

            titleView = cardView.findViewById(R.id.title_text);
            descriptionView = cardView.findViewById(R.id.description_text);
            buttonView = cardView.findViewById(R.id.button);
            checkMarkView = cardView.findViewById(R.id.check_mark);
            waitingIconView = cardView.findViewById(R.id.waiting_icon);
        }

        void setTitleText(String text) {
            titleView.setText(text);
        }

        void setDescriptionText(String text) {
            descriptionView.setText(text);
        }

        void setOnClick(View.OnClickListener listener) {
            buttonView.setOnClickListener(listener);
        }

        void setCheckMarkViewVisibility(boolean visible) {
            checkMarkView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }

        void setWaitingIconViewVisibility(boolean visible) {
            waitingIconView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}