package dc.danh.sofuser.view.screens;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dc.danh.sofuser.R;
import dc.danh.sofuser.controller.EnumManager;
import dc.danh.sofuser.controller.SOFApplication;
import dc.danh.sofuser.controller.network.SOFService;
import dc.danh.sofuser.controller.network.NetworkUtils;
import dc.danh.sofuser.model.SOFUserItem;
import dc.danh.sofuser.model.SOFUserObject;
import dc.danh.sofuser.model.database.SOFUser;
import dc.danh.sofuser.model.database.SOFUserDatabase;
import dc.danh.sofuser.view.adapter.AdapterSOFUser;
import dc.danh.sofuser.view.listenner.EndlessRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements AdapterSOFUser.SOFUserListener {
    private int mPaginate = 1;
    private SOFUserDatabase mDatabase;
    private List<SOFUser> mBookmarkedList;
    private long mLastBackPressTime = 0;
    private Toast mToast;

    @BindView(R.id.act_home_srl_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.act_home_rv_user)
    RecyclerView rvSOFUser;

    @BindView(R.id.act_home_pb_loadmore)
    ProgressBar pbLoadMore;

    Call<SOFUserObject> sofUserLisCall;

    @Inject
    SOFService sofService;

    @Inject
    AdapterSOFUser adapterSOFUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        HomeActivityComponent component = DaggerHomeActivityComponent.builder()
                .homeActivityModule(new HomeActivityModule(this))
                .sOFApplicationComponent(SOFApplication.get(this).component())
                .build();

        component.injectHomeActivity(this);
        setTitle(getResources().getString(R.string.sof_user_title));

        initDatabase();
        initRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this::getSOFUserList);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            getSOFUserList();
            getBookmarkListFromDB();
        });
    }

    private void initDatabase() {
        mDatabase = Room.databaseBuilder(HomeActivity.this, SOFUserDatabase.class, "SOFUser").build();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSOFUser.setLayoutManager(linearLayoutManager);
        rvSOFUser.setHasFixedSize(true);
        rvSOFUser.setNestedScrollingEnabled(false);
        rvSOFUser.setAdapter(adapterSOFUser);
        adapterSOFUser.setListener(this);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (adapterSOFUser.getViewType() == EnumManager.ViewType.All.getValue()) {
                    pbLoadMore.setVisibility(View.VISIBLE);
                    mPaginate = page + 1;
                    getSOFUserList();
                }
            }
        };
        rvSOFUser.addOnScrollListener(scrollListener);
    }

    @SuppressLint("StaticFieldLeak")
    private void getBookmarkListFromDB() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mBookmarkedList = mDatabase.sofUserDAO().getAllSOFUser();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapterSOFUser.addBookmarkList(mBookmarkedList);
            }
        }.execute();
    }

    private void getSOFUserList() {
        if (NetworkUtils.isNetworkAvailable(HomeActivity.this)) {
            int mPageSize = 30;
            sofUserLisCall = sofService.getSOFUserList(mPaginate, mPageSize);
            sofUserLisCall.enqueue(new Callback<SOFUserObject>() {
                @Override
                public void onResponse(Call<SOFUserObject> call, Response<SOFUserObject> response) {
                    pbLoadMore.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    adapterSOFUser.setViewType(EnumManager.ViewType.All.getValue());
                    if (response.body() != null) {
                        adapterSOFUser.addData(response.body().items);
                    }
                }

                @Override
                public void onFailure(Call<SOFUserObject> call, Throwable t) {
                    pbLoadMore.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    t.printStackTrace();
                    Toast.makeText(HomeActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            pbLoadMore.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sofUserLisCall != null) {
            sofUserLisCall.cancel();
        }
    }

    @Override
    public void onItemClick(int position, SOFUserItem sofUserItem) {
        Intent intent = new Intent(HomeActivity.this, ReputationActivity.class);
        intent.putExtra("user_id", sofUserItem.getUser_id());
        intent.putExtra("display_name", sofUserItem.getDisplay_name());
        intent.putExtra("location", sofUserItem.getLocation());
        intent.putExtra("reputation", sofUserItem.getReputation());
        intent.putExtra("badges_gold", sofUserItem.getBadge_counts().gold);
        intent.putExtra("badges_silver", sofUserItem.getBadge_counts().silver);
        intent.putExtra("badges_bronze", sofUserItem.getBadge_counts().bronze);
        intent.putExtra("last_access_date", sofUserItem.getLast_access_date());
        intent.putExtra("profile_image", sofUserItem.getProfile_image());
        startActivity(intent);
    }

    @Override
    public void onBookmarkClick(int position, SOFUserItem sofUserItem) {
        if (mBookmarkedList.size() == 0) {
            //Don't have any bookmark added
            addBookmark(sofUserItem);
        } else {
            boolean isBookmark = false;

            for (SOFUser sofUser : mBookmarkedList) {
                if (sofUser.getUserID() == sofUserItem.getUser_id()) {
                    isBookmark = true;
                    deleteBookmark(sofUser);
                }
            }

            if (!isBookmark) {
                addBookmark(sofUserItem);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteBookmark(SOFUser sofUser) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mDatabase.sofUserDAO().deleteSOFUser(sofUser.getUserID());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mBookmarkedList.remove(sofUser);
                adapterSOFUser.removeBookmark(sofUser);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void addBookmark(SOFUserItem sofUserItem) {
        new AsyncTask<SOFUser, Void, SOFUser>() {
            @Override
            protected SOFUser doInBackground(SOFUser... sofUsers) {
                SOFUser sofUser = new SOFUser();
                sofUser.setUserID(sofUserItem.getUser_id());

                Gson gson = new Gson();
                String sofUserString = gson.toJson(sofUserItem);
                sofUser.setUserDetail(sofUserString);

                mDatabase.sofUserDAO().insertAll(sofUser);
                return sofUser;
            }

            @Override
            protected void onPostExecute(SOFUser sofUser) {
                super.onPostExecute(sofUser);
                mBookmarkedList.add(sofUser);
                adapterSOFUser.addBookmark(sofUser);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_all_user) {
            adapterSOFUser.setViewType(EnumManager.ViewType.All.getValue());
            return true;
        } else if (id == R.id.action_bookmarked) {
            adapterSOFUser.setViewType(EnumManager.ViewType.Bookmarked.getValue());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.mLastBackPressTime < System.currentTimeMillis() - 1000) {
            mToast = Toast.makeText(this, getString(R.string.str_press_back_exit), Toast.LENGTH_SHORT);
            mToast.show();
            this.mLastBackPressTime = System.currentTimeMillis();
        } else {
            if (mToast != null) {
                mToast.cancel();
            }
            super.onBackPressed();
        }
    }
}
