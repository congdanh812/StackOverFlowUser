package dc.danh.sofuser.view.screens;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dc.danh.sofuser.R;
import dc.danh.sofuser.controller.SOFApplication;
import dc.danh.sofuser.controller.network.SOFService;
import dc.danh.sofuser.controller.network.NetworkUtils;
import dc.danh.sofuser.model.ReputationObject;
import dc.danh.sofuser.view.adapter.AdapterReputation;
import dc.danh.sofuser.view.listenner.EndlessRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReputationActivity extends AppCompatActivity {
    private int mPaginate = 1;
    private int mUserID;

    @BindView(R.id.act_rep_tv_display_name)
    TextView tvDisplayName;

    @BindView(R.id.act_rep_iv_profile_image)
    ImageView ivProfileImage;

    @BindView(R.id.act_rep_tv_reputation)
    TextView tvReputation;

    @BindView(R.id.act_rep_ll_gold_badges)
    LinearLayout llGoldBadges;

    @BindView(R.id.act_rep_tv_gold_badges)
    TextView tvGoldBadges;

    @BindView(R.id.act_rep_ll_silver_badges)
    LinearLayout llSilverBadges;

    @BindView(R.id.act_rep_tv_silver_badges)
    TextView tvSilverBadges;

    @BindView(R.id.act_rep_ll_bronze_badges)
    LinearLayout llBronzeBadges;

    @BindView(R.id.act_rep_tv_bronze_badges)
    TextView tvBronzeBadges;

    @BindView(R.id.act_rep_ll_location)
    LinearLayout llLocation;

    @BindView(R.id.act_rep_tv_location)
    TextView tvLocation;

    @BindView(R.id.act_rep_tv_last_access_date)
    TextView tvLastAccessDate;

    @BindView(R.id.act_rep_srl_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.act_rep_rv_reputation_history)
    RecyclerView rvReputationHistory;

    @BindView(R.id.act_rep_pb_loadmore)
    ProgressBar pbLoadMore;

    @Inject
    SOFService sofService;

    @Inject
    AdapterReputation adapterReputation;

    Call<ReputationObject> reputationHistoryCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reputation);
        ButterKnife.bind(this);

        ReputationComponent component = DaggerReputationComponent.builder()
                .reputationActivityModule(new ReputationActivityModule(this))
                .sOFApplicationComponent(SOFApplication.get(this).component())
                .build();

        component.injectReputationActivity(this);
        setTitle(getResources().getString(R.string.reputation_history));

        mUserID = getIntent().getIntExtra("user_id", -1);
        if (mUserID == -1)
            return;

        displayUserDetail();
        initRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this::getReputationHistoryList);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            pbLoadMore.setVisibility(View.GONE);
            getReputationHistoryList();
        });
    }

    private void displayUserDetail() {
        Locale locale = getResources().getConfiguration().locale;
        String display_name = getIntent().getStringExtra("display_name");
        String location = getIntent().getStringExtra("location");
        int reputation = getIntent().getIntExtra("reputation", 0);
        int badges_gold = getIntent().getIntExtra("badges_gold", 0);
        int badges_silver = getIntent().getIntExtra("badges_silver", 0);
        int badges_bronze = getIntent().getIntExtra("badges_bronze", 0);
        long last_access_date = getIntent().getLongExtra("last_access_date", 0);
        String profile_image = getIntent().getStringExtra("profile_image");

        tvReputation.setText(String.format(locale, "%,d", reputation));
        tvDisplayName.setText(display_name);
        if (location == null) {
            llLocation.setVisibility(View.GONE);
        } else {
            llLocation.setVisibility(View.VISIBLE);
        }
        tvLocation.setText(location);

        String goldBadgesDisplay = "0", silverBadgesDisplay = "0", bronzeBadgesDisplay = "0";

        if (badges_gold > 0) {
            goldBadgesDisplay = String.valueOf(badges_gold);
            llGoldBadges.setVisibility(View.VISIBLE);
        } else {
            llGoldBadges.setVisibility(View.GONE);
        }

        if (badges_silver > 0) {
            silverBadgesDisplay = String.valueOf(badges_silver);
            llSilverBadges.setVisibility(View.VISIBLE);
        } else {
            llSilverBadges.setVisibility(View.GONE);
        }

        if (badges_bronze > 0) {
            bronzeBadgesDisplay = String.valueOf(badges_bronze);
            llBronzeBadges.setVisibility(View.VISIBLE);
        } else {
            llBronzeBadges.setVisibility(View.GONE);
        }

        tvGoldBadges.setText(goldBadgesDisplay);
        tvSilverBadges.setText(silverBadgesDisplay);
        tvBronzeBadges.setText(bronzeBadgesDisplay);

        SimpleDateFormat fullDateTimeFormat = new SimpleDateFormat(getResources().getString(R.string.full_date_time_format), locale);
        String lastAccessDate = getResources().getString(R.string.last_acceess) + " " + fullDateTimeFormat.format(new Date(last_access_date * 1000));
        tvLastAccessDate.setText(lastAccessDate);

        Glide.with(ReputationActivity.this).load(profile_image).placeholder(R.drawable.sof_default_image).into(ivProfileImage);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvReputationHistory.setLayoutManager(linearLayoutManager);
        rvReputationHistory.setHasFixedSize(true);
        rvReputationHistory.setNestedScrollingEnabled(false);
        rvReputationHistory.setAdapter(adapterReputation);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pbLoadMore.setVisibility(View.VISIBLE);
                mPaginate = page + 1;
                getReputationHistoryList();
            }
        };
        rvReputationHistory.addOnScrollListener(scrollListener);
    }

    private void getReputationHistoryList() {
        if (NetworkUtils.isNetworkAvailable(ReputationActivity.this)) {
            int mPageSize = 30;
            reputationHistoryCall = sofService.getSOFUserReputationHistory(mUserID, mPaginate, mPageSize);
            reputationHistoryCall.enqueue(new Callback<ReputationObject>() {
                @Override
                public void onResponse(Call<ReputationObject> call, Response<ReputationObject> response) {
                    pbLoadMore.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.body() != null && response.body().items != null) {
                        adapterReputation.addData(response.body().items);
                    }
                }

                @Override
                public void onFailure(Call<ReputationObject> call, Throwable t) {
                    pbLoadMore.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    t.printStackTrace();
                    Toast.makeText(ReputationActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            pbLoadMore.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
