package com.jonkimbel.servicetest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static com.google.common.base.Preconditions.checkNotNull;

public class ActionCardListAdapter extends RecyclerView.Adapter<ActionCardListAdapter.ActionCardViewHolder> {
    private List<ActionCardViewModel> viewModels;

    public ActionCardListAdapter(List<ActionCardViewModel> viewModels) {
        this.viewModels = viewModels;
    }

    @Override
    public ActionCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collapsible_card_with_button, parent, false);
        return new ActionCardViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ActionCardViewHolder view, int position) {
        ActionCardViewModel viewModel = viewModels.get(position);

        view.setTitleText(viewModel.getTitleText());
        view.setDescriptionText(viewModel.getDescriptionText());
        view.setTimerText(viewModel.getTimerText());
        view.setOnClick(v -> viewModel.onClick());
        view.setCheckMarkViewVisibility(viewModel.getCheckMarkViewVisibility());
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public static class ActionCardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView timerView;
        private TextView descriptionView;
        private View buttonView;
        private View checkMarkView;

        public ActionCardViewHolder(MaterialCardView cardView) {
            super(cardView);

            Log.d("ActionCardListAdapter", "childCount:" + cardView.getChildCount());

            titleView = (TextView) cardView.findViewById(R.id.title_text);
            timerView = (TextView) cardView.findViewById(R.id.timer_text);
            descriptionView = (TextView) cardView.findViewById(R.id.description_text);
            buttonView =  cardView.findViewById(R.id.button);
            checkMarkView =  cardView.findViewById(R.id.check_mark);
        }

        public void setTitleText(String text) {
            titleView.setText(text);
        }

        public void setTimerText(String text) {
            timerView.setText(text);
        }

        public void setDescriptionText(String text) {
            descriptionView.setText(text);
        }

        public void setOnClick(View.OnClickListener listener) {
            buttonView.setOnClickListener(listener);
        }

        public void setCheckMarkViewVisibility(boolean visible) {
            checkMarkView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}