package dc.danh.sofuser.view.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dc.danh.sofuser.R;
import dc.danh.sofuser.controller.EnumManager;
import dc.danh.sofuser.model.SOFUserItem;
import dc.danh.sofuser.model.database.SOFUser;
import dc.danh.sofuser.view.screens.HomeActivity;

public class AdapterSOFUser extends RecyclerView.Adapter<AdapterSOFUser.ViewHolder> {
    private final List<SOFUserItem> mSofUserList = new ArrayList<>(0);
    private final List<SOFUser> mBookmarkedList = new ArrayList<>(0);

    private final Context context;
    private SOFUserListener mListener;
    private SimpleDateFormat mFullDateTimeFormat;
    private int mViewType;

    @Inject
    AdapterSOFUser(HomeActivity context) {
        this.context = context;
        Locale locale = context.getResources().getConfiguration().locale;
        mFullDateTimeFormat = new SimpleDateFormat(context.getResources().getString(R.string.full_date_time_format), locale);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sof_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        SOFUserItem sofUserItem = null;
        if (mViewType == EnumManager.ViewType.All.getValue()) {
            if (mSofUserList.size() > 0) {
                sofUserItem = mSofUserList.get(position);
            }
        } else {
            if (mBookmarkedList.size() > 0) {
                String userDetail = mBookmarkedList.get(position).getUserDetail();
                Gson gson = new Gson();
                Type type = new TypeToken<SOFUserItem>() {}.getType();
                sofUserItem = gson.fromJson(userDetail, type);
            }
        }
        if (sofUserItem != null) {
            holder.tvDisplayName.setText(sofUserItem.getDisplay_name());
            if (sofUserItem.getLocation() == null) {
                holder.llLocation.setVisibility(View.GONE);
            } else {
                holder.llLocation.setVisibility(View.VISIBLE);
            }
            holder.tvLocation.setText(sofUserItem.getLocation());

            String reputation, goldBadges, silverBadges, bronzeBadges;
            if (sofUserItem.getReputation() >= 1000) {
                reputation = (sofUserItem.getReputation() / 1000) + "k";
            } else {
                reputation = String.valueOf(sofUserItem.getReputation());
            }

            if (sofUserItem.getBadge_counts().gold > 0) {
                goldBadges = String.valueOf(sofUserItem.getBadge_counts().gold);
                holder.ivGoldBadges.setVisibility(View.VISIBLE);
                holder.tvGoldBadges.setVisibility(View.VISIBLE);
            } else {
                goldBadges = "0";
                holder.ivGoldBadges.setVisibility(View.GONE);
                holder.tvGoldBadges.setVisibility(View.GONE);
            }

            if (sofUserItem.getBadge_counts().silver > 0) {
                silverBadges = String.valueOf(sofUserItem.getBadge_counts().silver);
                holder.ivSilverBadges.setVisibility(View.VISIBLE);
                holder.tvSilverBadges.setVisibility(View.VISIBLE);
            } else {
                silverBadges = "0";
                holder.ivSilverBadges.setVisibility(View.GONE);
                holder.tvSilverBadges.setVisibility(View.GONE);
            }

            if (sofUserItem.getBadge_counts().bronze > 0) {
                bronzeBadges = String.valueOf(sofUserItem.getBadge_counts().bronze);
                holder.ivBronzeBadges.setVisibility(View.VISIBLE);
                holder.tvBronzeBadges.setVisibility(View.VISIBLE);
            } else {
                bronzeBadges = "0";
                holder.ivBronzeBadges.setVisibility(View.GONE);
                holder.tvBronzeBadges.setVisibility(View.GONE);
            }

            holder.tvReputation.setText(reputation);
            holder.tvGoldBadges.setText(goldBadges);
            holder.tvSilverBadges.setText(silverBadges);
            holder.tvBronzeBadges.setText(bronzeBadges);

            String lastAccessDate = mFullDateTimeFormat.format(new Date(sofUserItem.getLast_access_date() * 1000));
            holder.tvLastAccessDate.setText(lastAccessDate);

            Glide.with(context).load(sofUserItem.getProfile_image()).placeholder(R.drawable.sof_default_image).into(holder.ivProfileImage);

            SOFUserItem finalSofUserItem = sofUserItem;
            holder.ivBookmark.setOnClickListener(v -> mListener.onBookmarkClick(position, finalSofUserItem));

            holder.ivBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark));
            if (mBookmarkedList.size() > 0) {
                for (SOFUser sofUser : mBookmarkedList) {
                    if (sofUser.getUserID() == sofUserItem.getUser_id()) {
                        holder.ivBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmarked));
                    }
                }
            }

            holder.cardView.setOnClickListener(v -> mListener.onItemClick(position, finalSofUserItem));
        }
    }

    @Override
    public int getItemCount() {
        if (mViewType == EnumManager.ViewType.All.getValue()) {
            return mSofUserList.size();
        } else {
            return mBookmarkedList.size();
        }
    }

    public void addData(Collection<SOFUserItem> sofUserList) {
        if (sofUserList != null) {
            this.mSofUserList.addAll(sofUserList);
        }
        notifyDataSetChanged();
    }

    public void addBookmarkList(Collection<SOFUser> bookmarkList) {
        this.mBookmarkedList.addAll(bookmarkList);
        sortBookmarkList();
        notifyDataSetChanged();
    }

    public void addBookmark(SOFUser sofUser) {
        this.mBookmarkedList.add(sofUser);
        sortBookmarkList();
        notifyDataSetChanged();
    }

    public void removeBookmark(SOFUser sofUser) {
        this.mBookmarkedList.remove(sofUser);
        notifyDataSetChanged();
    }

    private void sortBookmarkList(){
        Collections.sort(mBookmarkedList, (o1, o2) -> {
            String userDetail1 = o1.getUserDetail();
            String userDetail2 = o2.getUserDetail();
            Gson gson = new Gson();
            Type type = new TypeToken<SOFUserItem>() {
            }.getType();
            SOFUserItem sofUserItem1 = gson.fromJson(userDetail1, type);
            SOFUserItem sofUserItem2 = gson.fromJson(userDetail2, type);
            return sofUserItem2.getReputation().compareTo(sofUserItem1.getReputation());
        });
    }

    public void setViewType(int viewType) {
        this.mViewType = viewType;
        notifyDataSetChanged();
    }

    public int getViewType() {
        return mViewType;
    }

    public void setListener(SOFUserListener sofUserListener) {
        mListener = sofUserListener;
    }

    public interface SOFUserListener {
        void onItemClick(int position, SOFUserItem sofUserItem);

        void onBookmarkClick(int position, SOFUserItem sofUserItem);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_sof_user_card_view)
        CardView cardView;

        @BindView(R.id.item_sof_user_iv_profile_image)
        ImageView ivProfileImage;

        @BindView(R.id.item_sof_user_tv_display_name)
        TextView tvDisplayName;

        @BindView(R.id.item_sof_user_ll_location)
        LinearLayout llLocation;

        @BindView(R.id.item_sof_user_tv_location)
        TextView tvLocation;

        @BindView(R.id.item_sof_user_tv_reputation)
        TextView tvReputation;

        @BindView(R.id.item_sof_user_iv_gold_badges)
        ImageView ivGoldBadges;

        @BindView(R.id.item_sof_user_tv_gold_badges)
        TextView tvGoldBadges;

        @BindView(R.id.item_sof_user_iv_silver_badges)
        ImageView ivSilverBadges;

        @BindView(R.id.item_sof_user_tv_silver_badges)
        TextView tvSilverBadges;

        @BindView(R.id.item_sof_user_iv_bronze_badges)
        ImageView ivBronzeBadges;

        @BindView(R.id.item_sof_user_tv_bronze_badges)
        TextView tvBronzeBadges;

        @BindView(R.id.item_sof_user_tv_last_access_date)
        TextView tvLastAccessDate;

        @BindView(R.id.item_sof_user_iv_bookmark)
        ImageView ivBookmark;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
