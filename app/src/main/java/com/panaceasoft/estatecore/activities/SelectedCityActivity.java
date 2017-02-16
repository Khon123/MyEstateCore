package com.panaceasoft.estatecore.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.panaceasoft.estatecore.Config;
import com.panaceasoft.estatecore.GlobalData;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.adapters.CategoryAdapter;
import com.panaceasoft.estatecore.listeners.ClickListener;
import com.panaceasoft.estatecore.listeners.RecyclerTouchListener;
import com.panaceasoft.estatecore.models.CategoryRowData;
import com.panaceasoft.estatecore.models.PCategoryData;
import com.panaceasoft.estatecore.models.PCityData;
import com.panaceasoft.estatecore.models.PSubCategoryData;
import com.panaceasoft.estatecore.utilities.BitmapTransform;
import com.panaceasoft.estatecore.utilities.Utils;
import com.squareup.picasso.Picasso;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class SelectedCityActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private Picasso p;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView detailImage;
    private TextView txtTitle;
    //    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private CategoryAdapter mAdapter;
    private List<CategoryRowData> myDataset = new ArrayList<>();
    private CategoryRowData info;
    private int selectedCityID;
    private int selectedCityIndex;
    private PCityData pCity;
    private ArrayList<PCategoryData> categoryArrayList;
    private ArrayList<PSubCategoryData> subCategoryArrayList;
    private CoordinatorLayout mainLayout;
    private RecyclerView mRecyclerView;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_city);

        initUI();

        initData();

        saveSelectedCityInfo(pCity);

        bindData();

        loadCategoryGrid();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_news) {


            Utils.psLog("Open News Activity");
            final  Intent intent;
            intent = new Intent(this, NewsListActivity.class);
            intent.putExtra("selected_city_id", selectedCityID + "");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    protected void onDestroy() {
        try {
            Utils.psLog("Clearing Objects on Destroy");

            mRecyclerView.addOnItemTouchListener(null);
            collapsingToolbar = null;
            toolbar = null;
            detailImage.setImageResource(0);
            detailImage = null;
            txtTitle = null;

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
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        initToolbar();
        initCollapsingToolbarLayout();

        mainLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        p =new Picasso.Builder(this).build();
                // For memory limit
                //.memoryCache(new LruCache(1))

    }

    private void initCollapsingToolbarLayout(){
        try {
            collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initCollapsingToolbarLayout.", e);
        }
    }

    private void initToolbar() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
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

            toolbar.setTitle("");
            toolbar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }

    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {
        try {
            detailImage = (ImageView) findViewById(R.id.detail_image);
            pCity = GlobalData.citydata;
            selectedCityID = pCity.id;
            selectedCityIndex = getIntent().getIntExtra("selected_city_index", 0);
        } catch (Exception e) {
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
            if(collapsingToolbar != null){

                collapsingToolbar.setTitle(Utils.getSpannableString(pCity.name));
                makeCollapsingToolbarLayoutLooksGood(collapsingToolbar);

            }
            int MAX_WIDTH = Utils.getScreenWidth();
            int MAX_HEIGHT = MAX_WIDTH;
            p.load(Config.APP_IMAGES_URL + pCity.cover_image_file)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .placeholder(R.drawable.ps_icon)
                    .into(detailImage);
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
    public void loadCategoryGrid() {
        try {
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new CategoryAdapter(myDataset, mRecyclerView, p);
            mRecyclerView.setAdapter(mAdapter);

            for(PCategoryData cd : pCity.categories) {
                //subCategoryArrayList = cd.sub_categories;

                info = new CategoryRowData();
                info.setCatName(cd.name);
                info.setCatImage(cd.cover_image_file);

                if(myDataset != null && info != null) {
                    myDataset.add(info);
                }
            }

            if(myDataset != null) {
                mAdapter.notifyItemInserted(myDataset.size());
            }

            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Utils.psLog("cat onClick : " + position);
                    onItemClicked(position);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            mRecyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in loadCategoryGrid.", e);
        }
    }

    public void onItemClicked(int position){
        final Intent intent;
        intent = new Intent(this,ItemListActivity.class);
        Utils.psLog("cat : " + position);
        intent.putExtra("selected_category_index", position);
        intent.putExtra("selected_city_id", selectedCityID);
        intent.putExtra("selected_city_index", selectedCityIndex);

        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    private void makeCollapsingToolbarLayoutLooksGood(CollapsingToolbarLayout collapsingToolbarLayout) {
        try {
            final Field field = collapsingToolbarLayout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);

            final Object object = field.get(collapsingToolbarLayout);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            ((TextPaint) tpf.get(object)).setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
            ((TextPaint) tpf.get(object)).setColor( ContextCompat.getColor(this, R.color.colorAccent));
        } catch (Exception ignored) {
        }
    }

    private void saveSelectedCityInfo(PCityData ct) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("_id", ct.id);
            editor.putString("_name", ct.name);
            editor.putString("_cover_image", ct.cover_image_file);
            editor.putString("_address", ct.location);
            editor.putString("_city_region_lat", ct.lat);
            editor.putString("_city_region_lng", ct.lng);
            editor.apply();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in saveSelectedCityInfo.", e);
        }
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
}
