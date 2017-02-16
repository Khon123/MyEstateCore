package com.panaceasoft.estatecore.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.estatecore.Config;
import com.panaceasoft.estatecore.GlobalData;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.adapters.ItemAdapter;
import com.panaceasoft.estatecore.listeners.ClickListener;
import com.panaceasoft.estatecore.listeners.RecyclerTouchListener;
import com.panaceasoft.estatecore.models.PCategoryData;
import com.panaceasoft.estatecore.models.PItemData;
import com.panaceasoft.estatecore.models.PSubCategoryData;
import com.panaceasoft.estatecore.uis.ProgressWheel;
import com.panaceasoft.estatecore.utilities.Utils;
import com.panaceasoft.estatecore.utilities.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class ItemListActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private Toolbar toolbar;
    private ArrayList<? extends Parcelable> categoryArrayList = null;
    private int selectedCategoryIndex = 0;
    private String selectedCategoryName = "";
    private int selectedCityId;
    private int selectedCityIndex;
    private int C_FRAGMENTS_TO_KEEP_IN_MEMORY=0;
    private ViewPager viewPager;
    private ArrayList<PCategoryData> categoriesList;
    private ArrayList<PSubCategoryData> subCategoriesList;
    private SharedPreferences prefs;
    private Adapter adapter;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private List<PItemData> myDataset;
    private ItemAdapter mAdapter;
    private Picasso p;
    private String jsonStatusSuccess;
    private List<PItemData> it;
    private ProgressWheel progressWheel;
    private SwipeRefreshLayout swipeRefreshLayout;
    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        initData();

        initUI();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list, menu);

        int i = 0;

        for(PCategoryData cd : categoriesList) {
            if(menu != null && cd != null) {
                menu.add(0, i, 0, cd.name);
                i++;
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            final Intent intent;
            intent = new Intent(this, ItemSearchActivity.class);
            intent.putExtra("selected_city_index", selectedCityIndex);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        } else {
            loadCategoryUI(id);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                mAdapter.updateItemLikeAndReviewCount(data.getIntExtra("selected_item_id",0), data.getStringExtra("like_count"), data.getStringExtra("review_count"));
                mAdapter.notifyDataSetChanged();

                //TabFragment fragment = (TabFragment) ((Adapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
                //fragment.refershLikeAndReview(data.getIntExtra("selected_item_id",0), data.getStringExtra("like_count"), data.getStringExtra("review_count"));
            }

        }

    }

    @Override
    protected void onDestroy() {

        try {
            adapter = null;
            Utils.unbindDrawables(findViewById(R.id.drawer_layout));
            progressWheel = null;
            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }

    }


    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        try {

            this.jsonStatusSuccess = getResources().getString(R.string.json_status_success);
            initRecyclerView();
            initFAB();
            initToolbar();
            initProgressWheel();
            initSwipeRefreshLayout();

            mAdapter.setOnLoadMoreListener(new ItemAdapter.OnLoadMoreListener() {

                @Override
                public void onLoadMore() {
                    //add progress item

                    if(myDataset != null) {
                        int from = myDataset.size();
                        myDataset.add(null);
                        mAdapter.notifyItemInserted(myDataset.size() - 1);

                        Log.d("API URL : ", Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityId + "/cat_id/" + selectedCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/form/" + from);
                        requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityId + "/cat_id/" + selectedCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/from/" + from);
                    }

                }

            });

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

            startLoading();

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
        }

    }

    private void initProgressWheel() {
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                stopLoading();
            }
        });
    }

    private void initFAB() {
        try {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFabClicked(v);
                }
            });
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initFAB.", e);
        }
    }

    private void initToolbar() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if(getSupportActionBar() != null ) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setTitle(Utils.getSpannableString(selectedCategoryName));
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    private void initRecyclerView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new ArrayList<>();

        mAdapter = new ItemAdapter(myDataset, mRecyclerView, p);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                onItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {
        try {
            categoriesList = GlobalData.citydata.categories;
            selectedCityId = getIntent().getIntExtra("selected_city_id", 0);
            selectedCityIndex = getIntent().getIntExtra("selected_city_index", 0);
            selectedCategoryIndex = categoriesList.get(getIntent().getIntExtra("selected_category_index", 0)).id;
            selectedCategoryName  = categoriesList.get(getIntent().getIntExtra("selected_category_index", 0)).name;

            Utils.psLog(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityId + "/cat_id/" + selectedCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/form/0");
            requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityId + "/cat_id/" + selectedCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/form/0");

            p = new Picasso.Builder(this).build();

            // For Memory Limit
            //.memoryCache(new LruCache(2000))
            //.build();

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    private void onFabClicked(View v) {
        //TabFragment fragment = (TabFragment) ((Adapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("_selected_city_id", selectedCityId);
        editor.putInt("_selected_cat_id", selectedCategoryIndex);
        editor.apply();

        final Intent intent;
        intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }


    private void loadCategoryUI(int id){
        Intent intent = new Intent(this,ItemListActivity.class);
        intent.putExtra("selected_category_index", id);
        intent.putExtra("selected_city_id", selectedCityId);
        startActivity(intent);
        this.finish();
    }

    private void requestData(String uri) {

        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccess)) {

                                if(myDataset != null) {
                                    if (myDataset.size() > 0) {
                                        myDataset.remove(myDataset.size() - 1);
                                        mAdapter.notifyItemRemoved(myDataset.size());
                                    }
                                }

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {
                                }.getType();
                                it = gson.fromJson(response.getString("data"), listType);

                                progressWheel.setVisibility(View.GONE);

                                for (PItemData pItem : it) {

                                    if(myDataset != null && pItem != null) {
                                        myDataset.add(pItem);
                                    }

                                }

                                stopLoading();

                                if(myDataset != null) {
                                    mAdapter.notifyItemInserted(myDataset.size());
                                    mAdapter.setLoaded();
                                }


                            } else {
                                stopLoading();
                                Utils.psLog("Error in loading CityList.");
                            }
                        } catch (JSONException e) {
                            stopLoading();
                            e.printStackTrace();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        //Log.d("Volley Error " , ex.getMessage());
                    }
                });

        //queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        request.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        //queue.add(request);


    }

    private void startLoading() {
        try {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
        }
    }

    private void stopLoading() {
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void onItemClicked(int position) {
        ((ItemListActivity) this).openActivity(myDataset.get(position).id, myDataset.get(position).city_id);
    }

    public void openActivity(int selected_item_id, int selected_city_id){
        final Intent intent;
        intent = new Intent(this, DetailActivity.class);
        Utils.psLog("Selected City ID : " + selectedCityId);
        intent.putExtra("selected_item_id", selected_item_id);
        intent.putExtra("selected_city_id", selectedCityId);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Static Class
     **------------------------------------------------------------------------------------------------*/
    public class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        public Adapter(FragmentManager fm) {
            super(fm);
        }
        public void addFragment(Fragment fragment, String title) {

            if(mFragments != null && fragment != null) {
                mFragments.add(fragment);
            }

            if(mFragmentTitles != null) {
                mFragmentTitles.add(title);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            if(mFragments != null) {
                return mFragments.size();
            }
            return 0;
        }

        @Override
        public SpannableString getPageTitle(int position) {
            return Utils.getSpannableString(mFragmentTitles.get(position));
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Static Class
     **------------------------------------------------------------------------------------------------*/


}
