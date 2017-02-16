package com.panaceasoft.estatecore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.estatecore.Config;
import com.panaceasoft.estatecore.GlobalData;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.activities.SelectedCityActivity;
import com.panaceasoft.estatecore.adapters.CityAdapter;
import com.panaceasoft.estatecore.listeners.ClickListener;
import com.panaceasoft.estatecore.listeners.RecyclerTouchListener;
import com.panaceasoft.estatecore.models.PCityData;
import com.panaceasoft.estatecore.uis.ProgressWheel;
import com.panaceasoft.estatecore.utilities.BitmapTransform;
import com.panaceasoft.estatecore.utilities.Utils;
import com.panaceasoft.estatecore.utilities.VolleySingleton;
import com.squareup.picasso.LruCache;
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

public class CitiesListFragment extends Fragment {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private RecyclerView mRecyclerView;
    private ProgressWheel progressWheel;
    private CityAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView display_message;
    private ArrayList<PCityData> pCityDataList;
    private ArrayList<PCityData> pCityDataSet;
    private NestedScrollView singleLayout;
    private TextView scCityName;
    private TextView scCityLocation;
    private TextView scCityAbout;
    private TextView scCityCatCount;
    private TextView scCityAgentCount;
    private TextView scCityItemCount;
    private ImageView scCityPhoto;
    private Button scCityExplore;
    private FrameLayout mainLayout;
    private String jsonStatusSuccessString;
    private String connectionError;
    private int MAX_WIDTH = Utils.getScreenWidth();
    private int MAX_HEIGHT = MAX_WIDTH;
    private Picasso p;
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Public Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public CitiesListFragment() {

    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cities_list, container, false);

        initUI(view);

        initData();

        return view;
    }

    @Override
    public void onDestroy() {

        try {
            mRecyclerView = null;

            progressWheel = null;
            swipeRefreshLayout = null;
            p.shutdown();
            Utils.unbindDrawables(mainLayout);
            GlobalData.citydata = null;
            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initUI(View view){

        mainLayout = (FrameLayout) view.findViewById(R.id.cities_layout);
        p =new Picasso.Builder(getActivity())
                .memoryCache(new LruCache(1))
                .build();

        initSingleUI(view);

        initSwipeRefreshLayout(view);

        initProgressWheel(view);

        initRecyclerView(view);

        startLoading();

    }

    private void initSingleUI(View view) {

        singleLayout =(NestedScrollView) view.findViewById(R.id.single_city_layout);
        scCityName = (TextView) view.findViewById(R.id.sc_city_name);
        scCityLocation = (TextView) view.findViewById(R.id.sc_city_loc);
        scCityAbout = (TextView) view.findViewById(R.id.sc_city_desc);
        scCityCatCount = (TextView) view.findViewById(R.id.txt_cat_count);
        scCityAgentCount = (TextView) view.findViewById(R.id.txt_agent_count);
        scCityItemCount = (TextView) view.findViewById(R.id.txt_item_count);
        scCityPhoto = (ImageView) view.findViewById(R.id.sc_city_photo);
        scCityExplore = (Button) view.findViewById(R.id.button_explore);

        int screenWidth = Utils.getScreenWidth();

        int rlWidth = (screenWidth/3) - 20;

        RelativeLayout r1 = (RelativeLayout) view.findViewById(R.id.rl_count1);
        RelativeLayout r2 = (RelativeLayout) view.findViewById(R.id.rl_count2);
        RelativeLayout r3 = (RelativeLayout) view.findViewById(R.id.rl_count3);

        r1.setMinimumWidth(rlWidth);
        r2.setMinimumWidth(rlWidth);
        r3.setMinimumWidth(rlWidth);

        scCityPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(pCityDataList !=null && pCityDataList.size() > 0) {
                    final Intent intent;
                    intent = new Intent(getActivity(), SelectedCityActivity.class);
                    GlobalData.citydata = pCityDataList.get(0);
                    intent.putExtra("selected_city_id", pCityDataList.get(0).id);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                }

            }
        });

        scCityExplore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(pCityDataList !=null && pCityDataList.size() > 0) {
                    final Intent intent;
                    intent = new Intent(getActivity(), SelectedCityActivity.class);
                    GlobalData.citydata = pCityDataList.get(0);
                    intent.putExtra("selected_city_id", pCityDataList.get(0).id);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                }

            }
        });



    }

    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(Config.APP_API_URL + Config.GET_ALL);
            }
        });
    }

    private void initProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
    }

    private void initRecyclerView(View view) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        display_message = (TextView) view.findViewById(R.id.display_message);
        display_message.setVisibility(View.GONE);

        pCityDataSet = new ArrayList<>();
        adapter = new CityAdapter(getActivity(), pCityDataSet, this.p);
        mRecyclerView.setAdapter(adapter);

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

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initData(){
        requestData(Config.APP_API_URL + Config.GET_ALL);

        jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
        connectionError = getResources().getString(R.string.connection_error);

    }

    private void requestData(String uri) {
        JsonObjectRequest request = new JsonObjectRequest(uri,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {
                                progressWheel.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PCityData>>() {
                                }.getType();

                                pCityDataList = gson.fromJson(response.getString("data"), listType);

                                if(pCityDataList != null) {
                                    Utils.psLog("City Count : " + pCityDataList.size());
                                    if (pCityDataList.size() > 1) {
                                        singleLayout.setVisibility(View.GONE);
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        updateDisplay();
                                    } else {
                                        mRecyclerView.setVisibility(View.GONE);
                                        singleLayout.setVisibility(View.VISIBLE);
                                        stopLoading();
                                        updateSingleDisplay();
                                    }

                                    updateGlobalCityList();
                                }

                            } else {
                                stopLoading();
                                Utils.psLog("Error in loading CityList.");
                            }
                        } catch (JSONException e) {
                            Utils.psErrorLogE("Error in loading CityList.", e);
                            stopLoading();
                            e.printStackTrace();
                        } catch (Exception e) {
                            Utils.psErrorLog("Error in loading." ,e);
                        }
                    }
                },


                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        progressWheel.setVisibility(View.GONE);
                        stopLoading();
                       /* NetworkResponse response = ex.networkResponse;
                        if (response != null && response.data != null) {

                        } else {*/
                        try {
                            display_message.setVisibility(View.VISIBLE);
                            display_message.setText(connectionError);
                        }catch (Exception e){
                            Utils.psErrorLogE("Error in Connection Url.", e);
                        }
                        //}

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void updateSingleDisplay() {
        try {
            if (pCityDataList != null && pCityDataList.size() > 0) {

                display_message.setVisibility(View.GONE);
                singleLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
                scCityName.setText(pCityDataList.get(0).name);
                scCityLocation.setText(pCityDataList.get(0).location);
                scCityAbout.setText(pCityDataList.get(0).description);
                scCityCatCount.setText(pCityDataList.get(0).category_count + " Categories");
                scCityAgentCount.setText(pCityDataList.get(0).agent_count + " Agents");
                scCityItemCount.setText(pCityDataList.get(0).item_count + " Items");

                p.load(Config.APP_IMAGES_URL + pCityDataList.get(0).cover_image_file)
                        .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                        .placeholder(R.drawable.ps_icon)
                        .into(scCityPhoto);

            }
        }catch(Exception e){
            Utils.psErrorLogE("Error in single display data binding.", e);
        }
    }

    private void updateGlobalCityList() {

        if(GlobalData.cityDatas != null) {
            GlobalData.cityDatas.clear();
        }

        if(pCityDataList != null) {
            for (int i = 0; i < pCityDataList.size(); i++) {
                if(pCityDataList.get(i) != null) {
                    GlobalData.cityDatas.add(pCityDataList.get(i));
                }
            }
        }


    }

    private void updateDisplay() {

        if (swipeRefreshLayout.isRefreshing()) {
            if(pCityDataSet != null) {
                pCityDataSet.clear();
                adapter.notifyDataSetChanged();
            }

            for (PCityData cd : pCityDataList) {
                if(cd != null && pCityDataSet != null) {
                    pCityDataSet.add(cd);
                }
            }
        } else {
            for (PCityData cd : pCityDataList) {

                if(cd != null && pCityDataSet != null) {
                    pCityDataSet.add(cd);
                }
            }
        }
        stopLoading();

        if(pCityDataSet != null) {
            adapter.notifyItemInserted(pCityDataSet.size());
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Bind Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void onItemClicked(int position) {
        Utils.psLog("Position : " + position);
        Intent intent;
        intent = new Intent(getActivity(),SelectedCityActivity.class);
        GlobalData.citydata = pCityDataList.get(position);
        intent.putExtra("selected_city_id", pCityDataList.get(position).id);
        intent.putExtra("selected_city_index", position);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    private void startLoading(){
        try{
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }catch (Exception e){}
    }

    private void stopLoading(){
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }catch (Exception e){}
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------










}
