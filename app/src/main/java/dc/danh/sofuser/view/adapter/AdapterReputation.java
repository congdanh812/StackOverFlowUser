package dc.danh.sofuser.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dc.danh.sofuser.R;
import dc.danh.sofuser.model.ReputationItem;
import dc.danh.sofuser.view.screens.ReputationActivity;

public class AdapterReputation extends RecyclerView.Adapter<AdapterReputation.ViewHolder> {
    private final List<ReputationItem> reputationItemList = new ArrayList<>(0);
    private final Context context;
    private SimpleDateFormat fullDateTimeFormat;

    @Inject
    AdapterReputation(ReputationActivity context) {
        this.context = context;
        Locale locale = context.getResources().getConfiguration().locale;
        fullDateTimeFormat = new SimpleDateFormat(context.getResources().getString(R.string.short_date_time_format), locale);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_reputation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        if (reputationItemList.size() > 0) {
            ReputationItem reputationItem = reputationItemList.get(position);
            if (reputationItem != null) {
                String reputationChange;
                if (reputationItem.getReputation_change() < 0) {
                    reputationChange = String.valueOf(reputationItem.getReputation_change());
                    holder.tvReputationChange.setTextColor(Color.parseColor("#7f0308"));
                } else if (reputationItem.getReputation_change() == 0) {
                    reputationChange = "0";
                    holder.tvReputationChange.setTextColor(Color.GRAY);
                } else {
                    reputationChange = "+" + reputationItem.getReputation_change();
                    holder.tvReputationChange.setTextColor(Color.parseColor("#49a065"));
                }
                holder.tvReputationChange.setText(reputationChange);

                String lastAccessDate = fullDateTimeFormat.format(new Date(reputationItem.getCreation_date() * 1000));
                holder.tvReputationDateCreated.setText(lastAccessDate);

                holder.tvReputationType.setText(reputationItem.getReputation_history_type());
                holder.tvReputationPostID.setText(String.valueOf(reputationItem.getPost_id()));

            }
        }
    }

    @Override
    public int getItemCount() {
        return reputationItemList.size();
    }

    public void addData(Collection<ReputationItem> reputationItems) {
        if (reputationItems != null) {
            this.reputationItemList.addAll(reputationItems);
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_reputation_tv_change)
        TextView tvReputationChange;

        @BindView(R.id.item_reputation_tv_create_date)
        TextView tvReputationDateCreated;

        @BindView(R.id.item_reputation_tv_type)
        TextView tvReputationType;

        @BindView(R.id.item_reputation_tv_post_id)
        TextView tvReputationPostID;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
