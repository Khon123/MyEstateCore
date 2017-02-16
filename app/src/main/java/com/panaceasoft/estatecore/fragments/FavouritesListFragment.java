package com.panaceasoft.estatecore.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.estatecore.Config;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.activities.DetailActivity;
import com.panaceasoft.estatecore.adapters.ItemAdapter;
import com.panaceasoft.estatecore.listeners.ClickListener;
import com.panaceasoft.estatecore.listeners.RecyclerTouchListener;
import com.panaceasoft.estatecore.models.PItemData;
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
 * Created by Panacea-Soft on 8/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class FavouritesListFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private List<PItemData> items;
    private List<PItemData> myDataset;
    private ItemAdapter mAdapter;
    private ProgressWheel progressWheel;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private TextView displayMessage;
    private SharedPreferences pref;
    private String noDataAvaiString;
    private String jsonStatusSuccessString;
    private Picasso p;
    private LinearLayout mainLayout;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        initData();

        initUI(view);

        return view;
    }


    @Override
    public void onDestroy() {

        try {
            mRecyclerView = null;
            mLayoutManager = null;

            mAdapter = null;
            progressWheel = null;

            myDataset = null;

            p.shutdown();
            Utils.unbindDrawables(mainLayout);

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.psLog("Result");
        mAdapter.updateItemLikeAndReviewCount(data.getIntExtra("selected_item_id",0), data.getStringExtra("like_count"), data.getStringExtra("review_count"));
        mAdapter.notifyDataSetChanged();
        initData();

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {

        p =new Picasso.Builder(getActivity()).build();

        // FOr memory limit
        //.memoryCache(new LruCache(1))
        //.build();
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());



        Utils.psLog(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/form/0");
        requestData(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/form/0");

        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            noDataAvaiString = getResources().getString(R.string.no_data_available);

        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI(View view){
        initProgressWheel(view);
        initRecyclerView(view);

        mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
        displayMessage = (TextView) view.findViewById(R.id.display_message);
        displayMessage.setVisibility(View.GONE);

        if(Config.SHOW_APMOB) {
            AdView mAdView = (AdView) view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            AdView mAdView = (AdView) view.findViewById(R.id.adView);
            mAdView.setVisibility(View.GONE);
        }
    }

    private void initProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
    }

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new ArrayList<>();

        mAdapter = new ItemAdapter(myDataset, mRecyclerView, p);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new ItemAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                //add progress item

                if(myDataset != null) {
                    int from = myDataset.size();
                    myDataset.add(null);
                    mAdapter.notifyItemInserted(myDataset.size() - 1);
                    Utils.psLog(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/from/" + from);
                    requestData(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/from/" + from);
                }
            }

        });

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
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
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void requestData(String uri) {
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                if(myDataset != null) {

                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<PItemData>>() {
                                    }.getType();
                                    items = gson.fromJson(response.getString("data"), listType);

                                    if(items != null) {
                                        if (myDataset.size() > 0) {
                                            myDataset.remove(myDataset.size() - 1);
                                            mAdapter.notifyItemRemoved(myDataset.size());
                                        }

                                        Utils.psLog("Count : " + items.size());

                                        progressWheel.setVisibility(View.GONE);
                                        for (PItemData pItem : items) {
                                            if(myDataset != null && pItem != null) {
                                                myDataset.add(pItem);
                                            }
                                        }
                                        mAdapter.notifyItemInserted(myDataset.size());
                                    }
                                }
                                mAdapter.setLoaded();
                            }else{

                                if(myDataset != null) {
                                    if (myDataset.size() > 0) {
                                        myDataset.remove(myDataset.size() - 1);
                                        mAdapter.notifyItemRemoved(myDataset.size());
                                    }
                                }

                                mAdapter.setLoaded();
                                progressWheel.setVisibility(View.GONE);

                                if(myDataset == null && myDataset.size()<=0) {
                                    displayMessage.setVisibility(View.VISIBLE);
                                    displayMessage.setText(noDataAvaiString);
                                }
                            }

                        } catch (JSONException e) {
                            displayMessage.setVisibility(View.VISIBLE);
                            displayMessage.setText(noDataAvaiString);

                        } catch (Exception ee){
                            Utils.psErrorLog("error", ee);
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                    }
                });

        request.setShouldCache(false);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void onItemClicked(int position) {
        //((ItemListActivity)getActivity()).openActivity(myDataset.get(position).id, myDataset.get(position).city_id);
//        final Intent intent;
//        intent = new Intent(getActivity(), DetailActivity.class);
//        intent.putExtra("selected_item_id", myDataset.get(position).id);
//        intent.putExtra("selected_city_id", myDataset.get(position).city_id);
//        startActivity(intent);

        final Intent intent;
        intent = new Intent(getActivity(), DetailActivity.class);
        Utils.psLog("Selected City ID : " + myDataset.get(position).city_id);
        intent.putExtra("selected_item_id", myDataset.get(position).id);
        intent.putExtra("selected_city_id", myDataset.get(position).city_id);
        intent.putExtra("currency_short_form", myDataset.get(position).currency_short_form);
        getActivity().startActivityForResult(intent, 0);
        //overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/


}
