package com.panaceasoft.estatecore.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.estatecore.Config;
import com.panaceasoft.estatecore.GlobalData;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.models.PItemData;
import com.panaceasoft.estatecore.models.PReviewData;
import com.panaceasoft.estatecore.utilities.BitmapTransform;
import com.panaceasoft.estatecore.utilities.Utils;
import com.panaceasoft.estatecore.utilities.VolleySingleton;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class DetailActivity extends AppCompatActivity {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private Toolbar toolbar;
    private ImageView detailImage;
    private TextView txtTitle;
    private ArrayList<PReviewData> itemReviewData;
    private MapView mMapView;
    private SharedPreferences pref;
    private TextView txtLikeCount;
    private TextView txtReviewCount;
    private TextView txtItemPrice;
    private TextView txtTotalReview;
    private TextView txtReviewMessage;
    private TextView txtNameTime;
    private TextView txtShopName;
    private TextView txtAddress;
    private TextView txtDescription;
    private TextView txtPriceRange;
    private TextView txtMeterRange;
    private TextView title;
    private ImageView userPhoto;
    private Button btnLike;
    private Button btnMoreReview;
    private Button btnInquiry;
    private FloatingActionButton fab;
    private int selectedItemId;
    private int selectedCityId;
    private Bundle bundle;
    private Intent intent;
    private Boolean isFavourite = false;
    private RatingBar getRatingBar;
    private RatingBar setRatingBar;
    private TextView ratingCount;
    private Animation animation;
    private String jsonStatusSuccessString;
    private Picasso p;
    private CoordinatorLayout mainLayout;
    private NestedScrollView nsv;
    private LinearLayout layoutTotalRoom;
    private LinearLayout layoutBedRoom;
    private LinearLayout layoutBathRoom;
    private TextView txtTotalRoom;
    private TextView txtBedRoom;
    private TextView txtBathRoom;
    private ImageView imgSell;
    private ImageView imgRent;
    private ImageView imgAgentPhoto;
    private TextView txtAgentName;
    private TextView txtAgentPhone;
    private TextView txtAgentEmail;
    private String currencySymbol;
    private String currencyShortForm;

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        initData();

        initUI(savedInstanceState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.psLog("OnActivityResult");
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                bindData();
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Intent in = new Intent();
            in.putExtra("selected_item_id", GlobalData.itemData.id);
            in.putExtra("like_count", GlobalData.itemData.like_count);
            in.putExtra("review_count", GlobalData.itemData.review_count);
            setResult(RESULT_OK, in);

            GlobalData.itemData = null;

            finish();
            overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
        }catch (Exception e){
            Utils.psErrorLogE("Error in BackPress.", e);
            finish();

        }

    }

    @Override
    public void onDestroy() {
        try {
            toolbar = null;
            detailImage = null;
            txtTitle = null;

            //itemReviewData;
            mMapView.onDestroy();
            mMapView = null;

            pref = null;
            txtLikeCount = null;
            txtReviewCount = null;
            txtItemPrice = null;
            txtTotalReview = null;
            txtReviewMessage = null;
            txtNameTime = null;
            txtShopName = null;
            txtAddress = null;
            txtDescription = null;
            txtPriceRange = null;
            txtMeterRange = null;
            title = null;
            userPhoto = null;
            btnLike = null;
            btnMoreReview = null;
            btnInquiry = null;
            fab = null;
            bundle = null;
            intent = null;

            getRatingBar = null;
            setRatingBar = null;
            ratingCount = null;
            animation = null;

            layoutTotalRoom = null;
            layoutBedRoom = null;
            layoutBathRoom = null;

            txtTotalRoom = null;
            txtBedRoom = null;
            txtBathRoom = null;

            imgSell = null;
            imgRent = null;

            imgAgentPhoto = null;
            txtAgentName = null;

            txtAgentPhone = null;
            txtAgentEmail = null;

            p.shutdown();

            Utils.unbindDrawables(mainLayout);
            mainLayout = null;
            GlobalData.itemData = null;

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData() {

        p =new Picasso.Builder(this)
                .memoryCache(new LruCache(1))
                .build();

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        selectedItemId = getIntent().getIntExtra("selected_item_id", 0);
        selectedCityId = getIntent().getIntExtra("selected_city_id", 0);

        currencySymbol = getIntent().getStringExtra("currency_symbol");
        currencyShortForm = getIntent().getStringExtra("currency_short_form");

        requestData(Config.APP_API_URL + Config.ITEMS_BY_ID + selectedItemId + "/city_id/" + selectedCityId);
        jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

        updateTouchCount(selectedItemId);
    }

    private void updateTouchCount(int selectedItemId) {
        try {
            final String URL = Config.APP_API_URL + Config.POST_TOUCH_COUNT + selectedItemId;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("city_id", selectedCityId + "");//String.valueOf(pref.getInt("_id", 0)));
                    doSubmit(URL, params, "touch");
        }catch (Exception e){
            Utils.psErrorLogE("Error in Touch Count.", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initUI(Bundle savedInstanceState) {

        try {

            mainLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

            nsv = (NestedScrollView) findViewById(R.id.nsv);

            nsv.setDescendantFocusability(mainLayout.FOCUS_BEFORE_DESCENDANTS);
            nsv.setFocusable(true);
            nsv.setFocusableInTouchMode(true);

            initToolbar();

            btnLike = (Button) findViewById(R.id.btn_like);
            btnLike.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtLikeCount = (TextView) findViewById(R.id.total_like_count);
            txtLikeCount.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtReviewCount = (TextView) findViewById(R.id.total_review_count);
            txtReviewCount.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtTotalReview = (TextView) findViewById(R.id.total_review);
            txtTotalReview.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtReviewMessage = (TextView) findViewById(R.id.review_message);
            txtReviewMessage.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtNameTime = (TextView) findViewById(R.id.name_time);
            txtNameTime.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtAddress = (TextView) findViewById(R.id.address);
            txtAddress.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtDescription = (TextView) findViewById(R.id.txtDescription);
            txtDescription.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtPriceRange = (TextView) findViewById(R.id.txtPriceRange);
            txtPriceRange.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtMeterRange = (TextView) findViewById(R.id.txtMeterRange);
            txtMeterRange.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtTitle = (TextView) findViewById(R.id.title);
            txtTitle.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            userPhoto = (ImageView) findViewById(R.id.user_photo);
            detailImage = (ImageView) findViewById(R.id.detail_image);

            btnMoreReview = (Button) findViewById(R.id.btn_more_review);
            btnMoreReview.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            btnInquiry = (Button) findViewById(R.id.btn_inquiry);
            btnInquiry.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            getRatingBar = (RatingBar) findViewById(R.id.get_rating);
            setRatingBar = (RatingBar) findViewById(R.id.set_rating);
            ratingCount = (TextView) findViewById(R.id.rating_count);
            animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.pop_out);

            layoutTotalRoom = (LinearLayout) findViewById(R.id.total_room_container);
            layoutBedRoom = (LinearLayout) findViewById(R.id.bed_room_container);
            layoutBathRoom = (LinearLayout) findViewById(R.id.bath_room_container);

            layoutTotalRoom.getLayoutParams().width = (Utils.getScreenWidth() / 3) - 50;
            layoutBedRoom.getLayoutParams().width =(Utils.getScreenWidth() / 3) - 50;
            layoutBathRoom.getLayoutParams().width = (Utils.getScreenWidth() / 3) - 50;

            txtTotalRoom = (TextView) findViewById(R.id.total_room_count);
            txtTotalRoom.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtBedRoom = (TextView) findViewById(R.id.total_bed_room_count);
            txtBedRoom.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtBathRoom = (TextView) findViewById(R.id.total_bath_room_count);
            txtBathRoom.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            imgSell = (ImageView) findViewById(R.id.img_sell);
            imgRent = (ImageView) findViewById(R.id.img_rent);

            imgAgentPhoto = (ImageView) findViewById(R.id.agent_photo);
            txtAgentName = (TextView) findViewById(R.id.agent_name);

            txtAgentPhone = (TextView) findViewById(R.id.agent_phone);
            txtAgentEmail = (TextView) findViewById(R.id.agent_email);


            fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFavourite(v);

                    Utils.psLog("Start Animation.");


                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Utils.psLog("Started Animation.");
                            if (isFavourite) {
                                isFavourite = false;
                                fab.setImageResource(R.drawable.ic_favorite_border);
                            } else {
                                isFavourite = true;
                                fab.setImageResource(R.drawable.ic_favorite_white);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }


                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    fab.clearAnimation();
                    fab.startAnimation(animation);
                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    doLike(v);

                    Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
                    btnLike.startAnimation(rotate);
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            });

            getRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (pref.getInt("_login_user_id", 0) != 0) {
                        ratingChanged(ratingBar, rating, fromUser);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.login_required,
                                Toast.LENGTH_LONG).show();
                        getRatingBar.setRating(0);
                    }
                }
            });

            initilizeMap(savedInstanceState);

            if(Config.SHOW_APMOB) {
                AdView mAdView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

            }else{
                AdView mAdView = (AdView) findViewById(R.id.adView);
                mAdView.setVisibility(View.GONE);

            }

            btnLike.requestFocus();

        }catch(Exception e){
            Utils.psErrorLogE("Error in Init UI.", e);
        }

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }



    private void initilizeMap(Bundle savedInstanceState) {
        if (Utils.isGooglePlayServicesOK(this)) {
            mMapView = (MapView) findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            try {
                MapsInitializer.initialize(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void bindData() {
        try {
            bindTitle();
            bindToolbarImage();
            bindCountValues();
            bindReview();
            bindDescription();
            bindShopInfo();
            bindFavourite(fab);
            bindLike(txtLikeCount);
            bindRate();
            bindPrice();
            bindSqMeter();
            bindRoomCount();
            bindSaleRent();
            bindAgentInfo();

        }catch (Exception e){
            Utils.psErrorLogE("Error in binding", e);
        }
    }

    private void bindTitle() {
        try {
            txtTitle.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
            txtTitle.setText(GlobalData.itemData.name);

        }catch (Exception e){}
    }

    private void bindPrice() {
        try {

            txtPriceRange.setText("Price Range (" + GlobalData.citydata.currency_short_form + ") : " + GlobalData.itemData.price_min + " - " + GlobalData.itemData.price_max);

        } catch (Exception e) {
            txtPriceRange.setText("Price Range (" + currencyShortForm + ") : " + GlobalData.itemData.price_min + " - " + GlobalData.itemData.price_max);
        }
    }

    private void bindSqMeter() {
        try {
            txtMeterRange.setText("Sq. Meter Range : " +  GlobalData.itemData.sq_meter_min + " - " + GlobalData.itemData.sq_meter_max);
        } catch (Exception e) {}
    }

    private void bindRoomCount() {
        try {

            if(GlobalData.itemData != null) {
                if (Integer.parseInt(GlobalData.itemData.total_rooms) > 1) {

                    txtTotalRoom.setText(GlobalData.itemData.total_rooms + " rooms");
                } else {
                    txtTotalRoom.setText(GlobalData.itemData.total_rooms + " room");
                }

                if (Integer.parseInt(GlobalData.itemData.bed_rooms) > 1) {
                    txtBedRoom.setText(GlobalData.itemData.bed_rooms + " rooms");
                } else {
                    txtBedRoom.setText(GlobalData.itemData.bed_rooms + " room");
                }

                if (Integer.parseInt(GlobalData.itemData.bath_rooms) > 1) {
                    txtBathRoom.setText(GlobalData.itemData.bath_rooms + " rooms");
                } else {
                    txtBathRoom.setText(GlobalData.itemData.bath_rooms + " room");
                }
            }

        } catch (Exception e) {
            Utils.psErrorLog("Error in bind room count.", e);
        }
    }

    private void bindAgentInfo() {
        try {
            if(GlobalData.itemData != null) {
                txtAgentName.setText("Offered By : " + GlobalData.itemData.agent_name);
                txtAgentPhone.setText(GlobalData.itemData.agent_phone);
                txtAgentEmail.setText(GlobalData.itemData.agent_email);
                p.load(Config.APP_IMAGES_URL + GlobalData.itemData.agent_photo).resize(150, 150).into(imgAgentPhoto);
            }
        } catch (Exception e) {
            Utils.psErrorLog("Error bind agent.", e);
        }
    }

    private void bindSaleRent() {
        try {
            if(Integer.parseInt(String.valueOf(GlobalData.itemData.for_sell)) == 1) {
                Drawable myDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_yes_black, null);
                imgSell.setImageDrawable(myDrawable);
            }

            if(Integer.parseInt(String.valueOf(GlobalData.itemData.for_rent)) == 1) {
                Drawable myDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_yes_black, null);
                imgRent.setImageDrawable(myDrawable);
            }

        } catch (Exception e) {}
    }

    int MAX_WIDTH = Utils.getScreenWidth();
    int MAX_HEIGHT = MAX_WIDTH;
    private void bindToolbarImage() {
        try {
            detailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });

            p.load(Config.APP_IMAGES_URL + GlobalData.itemData.images.get(0).path)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .into(detailImage);
        }catch(Exception e){
            Utils.psErrorLogE("Error in Bind Toolbar Image.", e);
        }
    }

    private void bindCountValues() {
        try {
            txtLikeCount.setText(" " + GlobalData.itemData.like_count + " ");
            txtReviewCount.setText(" " + GlobalData.itemData.review_count + " ");
        }catch(Exception e){
            Utils.psErrorLogE("Error in Bind Count.", e);
        }
    }

    private void bindReview() {
        try {
            itemReviewData = GlobalData.itemData.reviews;

            View view = null;
            txtNameTime.setVisibility(View.VISIBLE);
            txtReviewMessage.setVisibility(View.VISIBLE);
            btnMoreReview.setText(getString(R.string.view_more_review));

            if(itemReviewData != null) {
                int i = 0;
                if (itemReviewData.size() > 0) {
                    if (itemReviewData.size() == 1) {
                        txtTotalReview.setText(itemReviewData.size() + " " + getString(R.string.review));
                    } else {
                        txtTotalReview.setText(itemReviewData.size() + " " + getString(R.string.reviews));
                    }


                    PReviewData reviewData = itemReviewData.get(0);
                    txtNameTime.setText(reviewData.appuser_name + " " + "(" + reviewData.added + ")");
                    txtReviewMessage.setText(reviewData.review);
                    if (!reviewData.profile_photo.equals("")) {
                        Utils.psLog(" Loading User photo : " + Config.APP_IMAGES_URL + reviewData.profile_photo);
                        p.load(Config.APP_IMAGES_URL + reviewData.profile_photo).resize(150, 150).into(userPhoto);
                    } else {
                        userPhoto.setColorFilter(Color.argb(114, 114, 114, 114));
                    }


                } else {
                    txtTotalReview.setText(getString(R.string.no_review_count));
                    txtNameTime.setVisibility(View.GONE);
                    txtReviewMessage.setVisibility(View.GONE);
                    btnMoreReview.setText(getString(R.string.add_first_review));

                    Drawable myDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rate_review_black, null);
                    userPhoto.setImageDrawable(myDrawable);
                    //userPhoto.setColorFilter(Color.argb(-1, 114, 114, 114));

                }
            }
        }catch (Exception e){
            Utils.psErrorLogE("Error in Bind Reviews.", e);
        }
    }

    private void bindDescription() {
        try {
            txtDescription.setText(GlobalData.itemData.description);
        }catch (Exception e){
            Utils.psErrorLogE("error in binding description", e);
        }
    }

    private void bindShopInfo() {

        txtAddress.setText(GlobalData.itemData.address);

        try {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    googleMap.getUiSettings().setZoomControlsEnabled(false);


                    double latitude = Double.parseDouble(GlobalData.itemData.lat);
                    double longitude = Double.parseDouble(GlobalData.itemData.lng);

                    MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(GlobalData.itemData.name);
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            new LatLng(latitude, longitude)).zoom(15.1f).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {

                            nsv.scrollTo(0, 10);
                            btnLike.requestFocus();
                            Utils.psLog("Finish Camera update.");
                        }

                        @Override
                        public void onCancel() {
                            nsv.scrollTo(0, 10);
                            btnLike.requestFocus();
                            Utils.psLog("Cancel Camera update.");
                        }
                    });


                    googleMap.addMarker(marker);
                }
            });

            btnLike.requestFocus();
            nsv.scrollTo(0,10);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in map initialize.", e);
        }

    }

    private void bindFavourite(FloatingActionButton fab) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0) {
                final String URL = Config.APP_API_URL + Config.GET_FAVOURITE + GlobalData.itemData.id;
                Utils.psLog(URL);
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("city_id", selectedCityId+"" );//String.valueOf(pref.getInt("_id", 0)));
                getFavourite(URL, params, fab);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Favourite.", e);
        }
    }

    public void bindLike(View view) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0) {
                final String URL = Config.APP_API_URL + Config.GET_LIKE + GlobalData.itemData.id;
                Utils.psLog(URL);
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("city_id", selectedCityId + "");//String.valueOf(pref.getInt("_id", 0)));
                params.put("platformName","android");
                getLike(URL, params, view);
            }
        }catch (Exception e){
            Utils.psErrorLogE("Error in Bind Like. ", e);
        }
    }

    public void bindRate() {
        try {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_IS_RATE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("city_id", selectedCityId + "");//String.valueOf(pref.getInt("_id", 0)));
                    getRate(URL, params);
        }catch (Exception e){
            Utils.psErrorLogE("Error in bind Rating", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void getFavourite(String postURL, HashMap<String, String> params, final FloatingActionButton fab) {
        //RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");
                            String data_status = response.getString("data");
                            if (success_status.equals(jsonStatusSuccessString)) {
                                if(data_status.equals("yes")) {
                                    isFavourite = true;
                                    fab.setImageResource(R.drawable.ic_favorite_white);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (Exception e) {
                            Utils.psErrorLog("Error in loading." ,e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);
        // add the request object to the queue to be executed
        //mRequestQueue.add(req);
    }

    private void getLike(String postURL, HashMap<String, String> params, final View view) {
        //RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");

                            if (success_status.equals(jsonStatusSuccessString)) {
                                txtLikeCount.setText(response.getString("total"));
                                btnLike.setBackgroundResource(R.drawable.ic_done);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (Exception e) {
                            Utils.psErrorLog("Error in loading." ,e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);
        // add the request object to the queue to be executed
        //mRequestQueue.add(req);
    }

    private void requestData(String uri) {
        Utils.psLog("Item Detail " + uri);
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<PItemData>() {
                                }.getType();
                                GlobalData.itemData = gson.fromJson(response.getString("data"), listType);

                                if (GlobalData.itemData != null) {
                                    bindData();
                                }

                            } else {

                                Utils.psLog("Error in loading.");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        } catch (Exception e) {
                            Utils.psErrorLog("Error in loading." ,e);
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        Log.d("Volley Error ", ex.getMessage());
                    }
                });
        request.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        //RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        //mRequestQueue.add(request);
    }

    private void openGallery() {
        bundle = new Bundle();
        bundle.putParcelable("images", GlobalData.itemData);
        bundle.putString("from", "item");

        intent = new Intent(getApplicationContext(), GalleryActivity.class);
        intent.putExtra("images_bundle", bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    private void getRate(String postURL, HashMap<String, String> params) {
        //RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");
                            String data_status = response.getString("data");

                            if (success_status.equals(jsonStatusSuccessString)) {
                                JSONObject jresponse = new JSONObject(data_status);
                                if(jresponse.getString("isRate").equals("yes")){
                                    setRatingBar.setRating(Float.parseFloat(jresponse.getString("total")));
                                    if(Float.parseFloat(jresponse.getString("total")) != 0.0) {
                                        ratingCount.setText("Total Rating : " + jresponse.getString("total"));
                                    }
                                } else {
                                    ratingCount.setText(getString(R.string.first_rating));
                                }

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);
        // add the request object to the queue to be executed
        //mRequestQueue.add(req);
    }

    private void doSubmit(String postURL, HashMap<String, String> params, final String fromWhere) {
        //RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                if (fromWhere.equals("like")) {
                                    Utils.psLog("Count From Server : " + response.getString("data"));
                                    GlobalData.itemData.like_count = response.getString("data");
                                    txtLikeCount.setText(" " + GlobalData.itemData.like_count + " ");
                                }
                            } else {
                                showFailPopup();
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
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);
        // add the request object to the queue to be executed
        //mRequestQueue.add(req);
    }

    private void showFailPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.like_fail);
        builder.setPositiveButton("OK", null);
        builder.show();

    }

    private void showRatingFailPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.rating_fail);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void ratingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        Utils.psLog(String.valueOf(rating));

        final String URL = Config.APP_API_URL + Config.POST_ITEM_RATING + selectedItemId;
        Utils.psLog(URL);
        HashMap<String, String> params = new HashMap<>();
        params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
        params.put("rating", String.valueOf(rating));
        params.put("city_id", selectedCityId+"");//String.valueOf(pref.getInt("_id", 0)));

        //RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");

                            if (success_status.equals(jsonStatusSuccessString)) {
                                setRatingBar.setRating(Float.parseFloat(response.getString("data")));
                                ratingCount.setText("Total Rating : " + response.getString("data"));
                            } else {
                                showRatingFailPopup();
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
               Utils.psErrorLogE("Error in rating Change.", error);
            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);
        // add the request object to the queue to be executed
        //mRequestQueue.add(req);

    }

    private void showNeedLogin() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.login_required);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                startActivity(intent);
                Utils.psLog("OK clicked.");
            }
        });
        builder.show();
    }

    // Method to share either text or URL.
    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "CityDirectory");

        share.putExtra(Intent.EXTRA_TEXT, "http://codecanyon.net/user/panacea-soft/portfolio");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void doPhoneCall(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pref.getString("_phone", "")));
            startActivity(intent);
        }catch (SecurityException se ){
            Utils.psErrorLog("Security Exception. ", se);
        }
    }

    public void doEmail(View view) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{pref.getString("_email", "")});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public void doInquiry(View view) {
        final Intent intent;
        intent = new Intent(this, InquiryActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public void doReview(View view) {
        if(itemReviewData != null) {
            if (itemReviewData.size() > 0) {
                Intent intent = new Intent(this, ReviewListActivity.class);
                intent.putExtra("selected_item_id", selectedItemId);
                intent.putExtra("selected_city_id", selectedCityId);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            } else {
                if (pref.getInt("_login_user_id", 0) != 0) {
                    Intent intent = new Intent(this, ReviewEntry.class);
                    intent.putExtra("selected_item_id", selectedItemId);
                    intent.putExtra("selected_city_id", selectedCityId);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                } else {
                    Intent intent = new Intent(this, UserLoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                }
            }
        }
    }

    public void doFavourite(View view) {
        if (pref.getInt("_login_user_id", 0) != 0) {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_FAVOURITE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("city_id", selectedCityId + "");//String.valueOf(pref.getInt("_id", 0)));
            params.put("platformName","android");
            doSubmit(URL, params, "favourite");
        } else {
            if (isFavourite) {
                isFavourite = false;
                fab.setImageResource(R.drawable.ic_favorite_border);
            } else {
                isFavourite = true;
                fab.setImageResource(R.drawable.ic_favorite_white);
            }
            showNeedLogin();
        }
    }

    public void doLike(View view) {
        if (pref.getInt("_login_user_id", 0) != 0) {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_LIKE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("city_id", selectedCityId+"");//String.valueOf(pref.getInt("_id", 0)));
            params.put("platformName","android");
            doSubmit(URL, params, "like");
        } else {
            showNeedLogin();
        }
    }

    public void doShare(View view) {

        shareTextUrl();

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


}