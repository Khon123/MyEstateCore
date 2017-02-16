package com.panaceasoft.estatecore.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
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
import com.panaceasoft.estatecore.models.PItemData;
import com.panaceasoft.estatecore.utilities.Utils;
import com.panaceasoft.estatecore.utilities.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultActivity extends ActionBarActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private ItemAdapter mAdapter;
    private List<PItemData> myDataset;
    private Intent intent;
    private List<PItemData> it;
    private PItemData pit;
    private TextView txtSelectedCity;
    private TextView txtSearchKeyword;
    private TextView txtRecordFound;
    private String jsonStatusSuccessString;
    private SpannableString searchResultString;
    private Picasso p =null;
    private CoordinatorLayout mainLayout;
    private LinearLayout cityContainer;
    private LinearLayout keywordContainer;

    private TextView txtSelectedCategory;
    private LinearLayout categoryContainer;
    private TextView txtTotalRooms;
    private LinearLayout totalRoomsContainer;
    private TextView txtBedRooms;
    private LinearLayout bedRoomsContainer;
    private TextView txtBathRooms;
    private LinearLayout bathRoomsContainer;
    private TextView txtPriceRange;
    private LinearLayout priceRangeContainer;
    private TextView txtMeterRange;
    private LinearLayout meterRangeContainer;
    private TextView txtForSell;
    private TextView txtForRent;
    private LinearLayout sellRentContainer;

    private int bathRooms = 0;
    private int bedRooms = 0;
    private int totalRooms = 0;
    private int priceMin = 0;
    private int priceMax = 0;
    private int meterMin = 0;
    private int meterMax =0 ;
    private int forSell = 0;
    private int forRent = 0;
    private int catId = 0;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        initData();

        initUI();

        bindData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        initToolbar();
        initRecyclerView();

        mainLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        cityContainer = (LinearLayout) findViewById(R.id.selected_city_container);
        keywordContainer = (LinearLayout) findViewById(R.id.keyword_container);
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
            toolbar.setTitle(searchResultString);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init initToolbar.", e);
        }
    }

    public void initRecyclerView() {
        try {
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
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init initRecyclerView.", e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            Utils.psLog("Clearing Objects on Destroy");

            toolbar = null;

            mLayoutManager = null;

            myDataset.clear();

            mAdapter = null;
            myDataset = null;

            p.shutdown();
            Utils.unbindDrawables(mainLayout);

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }

    }



    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData(){
        try {

            p =new Picasso.Builder(this).build();

            // For memory limit
            //.memoryCache(new LruCache(1))
            //.build();
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            searchResultString = Utils.getSpannableString(getString(R.string.search_result));
        }catch(Exception e){
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void bindData() {
        try {
            final String URL;
            txtRecordFound = (TextView) findViewById(R.id.record_found);
            if(getIntent().getStringExtra("from_where").equals("keyword")) {
                txtSelectedCity = (TextView) findViewById(R.id.search_city);
                txtSelectedCity.setText(getIntent().getStringExtra("selected_city_name"));
                txtSearchKeyword = (TextView) findViewById(R.id.search_keyword);
                txtSearchKeyword.setText(getIntent().getStringExtra("search_keyword"));

                categoryContainer = (LinearLayout) findViewById(R.id.category_container);
                categoryContainer.setVisibility(View.GONE);

                totalRoomsContainer = (LinearLayout) findViewById(R.id.total_rooms_container);
                totalRoomsContainer.setVisibility(View.GONE);

                bedRoomsContainer = (LinearLayout) findViewById(R.id.bed_rooms_container);
                bedRoomsContainer.setVisibility(View.GONE);

                bathRoomsContainer = (LinearLayout) findViewById(R.id.bath_rooms_container);
                bathRoomsContainer.setVisibility(View.GONE);

                priceRangeContainer = (LinearLayout) findViewById(R.id.price_range_container);
                priceRangeContainer.setVisibility(View.GONE);

                meterRangeContainer = (LinearLayout) findViewById(R.id.meter_range_container);
                meterRangeContainer.setVisibility(View.GONE);

                sellRentContainer = (LinearLayout) findViewById(R.id.sell_rent_container);
                sellRentContainer.setVisibility(View.GONE);

                URL = Config.APP_API_URL + Config.POST_KEYWORD_SEARCH + getIntent().getStringExtra("selected_city_id");
                Utils.psLog(URL);
                doSearchKeyword(this, URL, getIntent().getStringExtra("search_keyword"));
            } else if(getIntent().getStringExtra("from_where").equals("item")) {
                cityContainer.setVisibility(View.GONE);
                keywordContainer.setVisibility(View.GONE);



                if(getIntent().getStringExtra("selected_cat_name") != null) {
                    txtSelectedCategory = (TextView) findViewById(R.id.txt_category_name);
                    txtSelectedCategory.setText(getIntent().getStringExtra("selected_cat_name"));
                    catId = Integer.parseInt(getIntent().getStringExtra("selected_cat_id"));
                } else {
                    categoryContainer = (LinearLayout) findViewById(R.id.category_container);
                    categoryContainer.setVisibility(View.GONE);

                }

                if(Integer.parseInt(getIntent().getStringExtra("total_rooms")) != 0) {
                    txtTotalRooms = (TextView) findViewById(R.id.txt_total_rooms);
                    txtTotalRooms.setText(getIntent().getStringExtra("total_rooms"));
                    totalRooms = Integer.parseInt(getIntent().getStringExtra("total_rooms"));
                } else {
                    totalRoomsContainer = (LinearLayout) findViewById(R.id.total_rooms_container);
                    totalRoomsContainer.setVisibility(View.GONE);

                }

                if(Integer.parseInt(getIntent().getStringExtra("bed_rooms")) != 0) {
                    txtBedRooms = (TextView) findViewById(R.id.txt_bed_rooms);
                    txtBedRooms.setText(getIntent().getStringExtra("bed_rooms"));
                    bedRooms = Integer.parseInt(getIntent().getStringExtra("bed_rooms"));
                } else {
                    bedRoomsContainer = (LinearLayout) findViewById(R.id.bed_rooms_container);
                    bedRoomsContainer.setVisibility(View.GONE);
                }

                if(Integer.parseInt(getIntent().getStringExtra("bath_rooms")) != 0) {
                    txtBathRooms = (TextView) findViewById(R.id.txt_bath_rooms);
                    txtBathRooms.setText(getIntent().getStringExtra("bath_rooms"));
                    bathRooms = Integer.parseInt(getIntent().getStringExtra("bath_rooms"));
                } else {
                    bathRoomsContainer = (LinearLayout) findViewById(R.id.bath_rooms_container);
                    bathRoomsContainer.setVisibility(View.GONE);
                }

                if(Integer.parseInt(getIntent().getStringExtra("price_min")) != 0 && Integer.parseInt(getIntent().getStringExtra("price_max")) != 0 ) {
                    txtPriceRange = (TextView) findViewById(R.id.txt_price_range);
                    txtPriceRange.setText(GlobalData.citydata.currency_symbol + getIntent().getStringExtra("price_min")
                    + " - " + GlobalData.citydata.currency_symbol + getIntent().getStringExtra("price_max"));
                    priceMin = Integer.parseInt(getIntent().getStringExtra("price_min"));
                    priceMax = Integer.parseInt(getIntent().getStringExtra("price_max"));
                } else {
                    priceRangeContainer = (LinearLayout) findViewById(R.id.price_range_container);
                    priceRangeContainer.setVisibility(View.GONE);
                }

                if(Integer.parseInt(getIntent().getStringExtra("meter_min")) != 0 && Integer.parseInt(getIntent().getStringExtra("meter_max")) != 0 ) {
                    txtMeterRange = (TextView) findViewById(R.id.txt_meter_range);
                    txtMeterRange.setText(getIntent().getStringExtra("meter_min") +" - "+ getIntent().getStringExtra("meter_max"));
                    meterMin = Integer.parseInt(getIntent().getStringExtra("meter_min"));
                    meterMax = Integer.parseInt(getIntent().getStringExtra("meter_max"));
                } else {
                    meterRangeContainer = (LinearLayout) findViewById(R.id.meter_range_container);
                    meterRangeContainer.setVisibility(View.GONE);
                }


                if(Integer.parseInt(getIntent().getStringExtra("for_sell")) == 1 && Integer.parseInt(getIntent().getStringExtra("for_rent")) == 1) {
                    txtForSell = (TextView) findViewById(R.id.txt_for_sell);
                    txtForSell.setText("Yes");

                    txtForRent = (TextView) findViewById(R.id.txt_for_rent);
                    txtForRent.setText("Yes");

                    forSell = Integer.parseInt(getIntent().getStringExtra("for_sell"));
                    forRent = Integer.parseInt(getIntent().getStringExtra("for_rent"));

                } else if(Integer.parseInt(getIntent().getStringExtra("for_sell")) == 1 && Integer.parseInt(getIntent().getStringExtra("for_rent")) == 0) {
                    txtForSell = (TextView) findViewById(R.id.txt_for_sell);
                    txtForSell.setText("Yes");
                    forSell = Integer.parseInt(getIntent().getStringExtra("for_sell"));
                } else if(Integer.parseInt(getIntent().getStringExtra("for_sell")) == 0 && Integer.parseInt(getIntent().getStringExtra("for_rent")) == 1) {
                    txtForRent = (TextView) findViewById(R.id.txt_for_rent);
                    txtForRent.setText("Yes");
                    forRent = Integer.parseInt(getIntent().getStringExtra("for_rent"));
                } else if (Integer.parseInt(getIntent().getStringExtra("for_sell")) == 0 && Integer.parseInt(getIntent().getStringExtra("for_rent")) == 0) {
                    sellRentContainer = (LinearLayout) findViewById(R.id.sell_rent_container);
                    sellRentContainer.setVisibility(View.GONE);
                }
                URL = Config.APP_API_URL + Config.POST_ITEM_SEARCH + getIntent().getStringExtra("selected_city_id");
                Utils.psLog(URL);
                doSearchItem(this, URL, catId, totalRooms, bedRooms, bathRooms, priceMin, priceMax, meterMin, meterMax, forSell, forRent);

            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/
    public void doSearchItem(Context context, final String URL, final int catId, final int totalRooms, final int bedRooms, final int bathRooms,
                             final int priceMin, final int priceMax, final int meterMin, final int meterMax, final int forSell, final int forRent) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cat_id", String.valueOf(catId));
        params.put("total_rooms", String.valueOf(totalRooms));
        params.put("bed_rooms", String.valueOf(bedRooms));
        params.put("bath_rooms", String.valueOf(bathRooms));
        params.put("price_min", String.valueOf(priceMin));
        params.put("price_max", String.valueOf(priceMax));
        params.put("meter_min", String.valueOf(meterMin));
        params.put("meter_max", String.valueOf(meterMax));
        params.put("for_sell", String.valueOf(forSell));
        params.put("for_rent", String.valueOf(forRent));

        Utils.psLog("Cat ID : " + catId);
        Utils.psLog("Total Rooms : " + totalRooms);
        Utils.psLog("Bed Rooms : " + bedRooms);
        Utils.psLog("Bath Rooms : " + bathRooms);
        Utils.psLog("Price Min : " + priceMin);
        Utils.psLog("Price Max : " + priceMax);
        Utils.psLog("Meter Min : " + meterMin);
        Utils.psLog("Meter Max : " + meterMax);
        Utils.psLog("Sell : " + forSell);
        Utils.psLog("Rent : " + forRent);


        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {


                                if (myDataset!= null && myDataset.size() > 0) {
                                    myDataset.remove(myDataset.size() - 1);
                                    mAdapter.notifyItemRemoved(myDataset.size());
                                }

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {}.getType();
                                it = gson.fromJson(response.getString("data"), listType);

                                if(it != null) {
                                    txtRecordFound.setText("Search Result Count : " + it.size());
                                }else {
                                    txtRecordFound.setText("Search Result Count : 0");
                                }

                                for (PItemData pItem : it) {
                                    if(pItem != null && myDataset != null) {
                                        myDataset.add(pItem);
                                    }
                                }

                                if(myDataset != null) {
                                    mAdapter.notifyItemInserted(myDataset.size());
                                    mAdapter.setLoaded();
                                }


                            } else {

                                Utils.psLog("Error in loading.");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        sr.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(sr);


    }


    public void doSearchKeyword(Context context, final String URL, final String keyword) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("keyword", keyword);

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

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

                                if(it != null) {
                                    txtRecordFound.setText("Search Result Count : " + it.size());
                                }else {
                                    txtRecordFound.setText("Search Result Count : 0");
                                }

                                for (PItemData pItem : it) {
                                    if(pItem != null && myDataset != null) {
                                        myDataset.add(pItem);
                                    }
                                }

                                if(myDataset != null) {
                                    mAdapter.notifyItemInserted(myDataset.size());
                                }

                                mAdapter.setLoaded();

                            } else {

                                Utils.psLog("Error in loading.");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        } catch (Exception e) {
                            Utils.psErrorLog("Error in loading." ,e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        sr.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(sr);
    }

    public void onItemClicked(int position) {
        final Intent intent;
        intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("selected_item_id", myDataset.get(position).id);
        intent.putExtra("selected_city_id", myDataset.get(position).city_id );
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left,R.anim.blank_anim);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

}