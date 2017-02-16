package com.panaceasoft.estatecore.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.panaceasoft.estatecore.GlobalData;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.listeners.SelectListener;
import com.panaceasoft.estatecore.uis.PSNumberSelector;
import com.panaceasoft.estatecore.uis.PSPopupSingleSelectView;
import com.panaceasoft.estatecore.uis.RangeSeekBar;
import com.panaceasoft.estatecore.utilities.Utils;
import android.widget.CheckBox;
import android.widget.TextView;

public class ItemSearchActivity extends ActionBarActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private Toolbar toolbar;
    private SpannableString inquiry;
    private LinearLayout popupContainer;
    private LinearLayout numberSelectContainer;
    private int selectedCatId;
    private int selectedCityIndex;
    private String selectedCatName;
    private String selectCatString;
    private CheckBox cbSell;
    private CheckBox cbRent;
    private LinearLayout priceRangeBarContainer;
    private LinearLayout meterRangeBarContainer;
    private TextView txtPriceRange;
    private TextView txtMeterRange;
    private Button btn_search;
    private Intent intent;
    private PSNumberSelector totalRoomsCount;
    private PSNumberSelector bedRoomsCount;
    private PSNumberSelector bathRoomsCount;
    private int priceMin = 0;
    private int priceMax = 0;
    private int meterMin = 0;
    private int meterMax =0 ;
    private int forSell = 0;
    private int forRent = 0;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_search);
        initData();
        initUI();

        bindData();
    }

    @Override
    public void onDestroy() {
        try {
            toolbar = null;
            cbSell = null;
            cbRent = null;
            txtPriceRange = null;
            txtMeterRange = null;
            priceRangeBarContainer = null;
            meterRangeBarContainer = null;
            numberSelectContainer = null;
            popupContainer = null;

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
            initToolbar();
            cbSell = (CheckBox) findViewById(R.id.cb_sell);
            cbRent = (CheckBox) findViewById(R.id.cb_rent);
            txtPriceRange = (TextView) findViewById(R.id.txt_price_range);
            txtMeterRange = (TextView) findViewById(R.id.txt_meter_range);

            priceRangeBarContainer = (LinearLayout) findViewById(R.id.price_range_bar_container);
            meterRangeBarContainer = (LinearLayout) findViewById(R.id.meter_range_bar_container);
            numberSelectContainer = (LinearLayout) findViewById(R.id.number_select_container);

            popupContainer = (LinearLayout) findViewById(R.id.choose_container);
            popupContainer.removeAllViews();

            Utils.psLog("City ID : " + getIntent().getIntExtra("selected_city_id", 0));

            PSPopupSingleSelectView psPopupSingleSelectView = new PSPopupSingleSelectView(this, selectCatString, GlobalData.cityDatas.get(selectedCityIndex).categories, "category");
            psPopupSingleSelectView.setOnSelectListener(new SelectListener() {
                @Override
                public void Select(View view, int position, CharSequence text) {
                }

                @Override
                public void Select(View view, int position, CharSequence text, int id) {
                    selectedCatId = id;
                    selectedCatName = text.toString();
                    Utils.psLog("Selected Cat Name at Search Page " + selectedCatName);

                }

                @Override
                public void Select(View view, int position, CharSequence text, int id, float additionalPrice) {
                }
            });

            popupContainer.addView(psPopupSingleSelectView);

            totalRoomsCount = new PSNumberSelector(this, getResources().getString(R.string.total_rooms), 10);
            numberSelectContainer.addView(totalRoomsCount);

            bedRoomsCount = new PSNumberSelector(this, getResources().getString(R.string.bed_rooms), 10);
            numberSelectContainer.addView(bedRoomsCount);

            bathRoomsCount = new PSNumberSelector(this, getResources().getString(R.string.bath_rooms), 10);
            numberSelectContainer.addView(bathRoomsCount);

            cbSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.psLog("Sell Check status : " + ((CheckBox) v).isChecked());
                    if(((CheckBox) v).isChecked()) {
                        forSell = 1;
                    } else {
                        forSell = 0;
                    }
                }
            });

            cbRent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.psLog("Rent Check status : " + ((CheckBox) v).isChecked());
                    if(((CheckBox) v).isChecked()) {
                        forRent = 1;
                    } else {
                        forRent = 0;
                    }
                }
            });


            RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(1, 100000000, this);
            seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                    //txtPriceRange.setText((10 * minValue) + " - " + (10 * maxValue) + " Price Range");
                    txtPriceRange.setText((minValue) + " - " + (maxValue) + " Price Range");
                    Utils.psLog("User selected new price range values: MIN=" + minValue + ", MAX=" + maxValue);
                    priceMin = minValue;
                    priceMax = maxValue;
                }
            });

            priceRangeBarContainer.addView(seekBar);

            RangeSeekBar<Integer> meterSeekBar = new RangeSeekBar<Integer>(1, 10000, this);
            meterSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {

                    //txtMeterRange.setText((10*minValue) + " - " + (10*maxValue) + " Meter Range");
                    txtMeterRange.setText((minValue) + " - " + (maxValue) + " Meter Range");
                    // handle changed range values
                    Utils.psLog("User selected new meter range values: MIN=" + minValue + ", MAX=" + maxValue);
                    meterMin = minValue;
                    meterMax = maxValue;
                }
            });

            meterRangeBarContainer.addView(meterSeekBar);

            btn_search = (Button) findViewById(R.id.button_search);
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(this, " Count is : " + psNumberSelector.VALUE, Toast.LENGTH_SHORT).show();
                    //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    prepareForSearch(v);
                }
            });


        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
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
        } catch (Exception e) {
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
            inquiry = Utils.getSpannableString(getString(R.string.search_criteria));
            selectCatString = getResources().getString(R.string.select_category);
            //pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            selectedCityIndex = getIntent().getIntExtra("selected_city_index", 0);

        } catch (Resources.NotFoundException e) {
            Utils.psErrorLog("Error in initData,", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {
        toolbar.setTitle(inquiry);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void prepareForSearch(View v) {
        intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("selected_cat_id", selectedCatId + "");
        intent.putExtra("selected_cat_name", selectedCatName);
        intent.putExtra("total_rooms", totalRoomsCount.VALUE + "");
        intent.putExtra("bed_rooms", bedRoomsCount.VALUE + "");
        intent.putExtra("bath_rooms", bathRoomsCount.VALUE + "");
        intent.putExtra("price_min", priceMin + "");
        intent.putExtra("price_max", priceMax + "");
        intent.putExtra("meter_min", meterMin + "");
        intent.putExtra("meter_max", meterMax + "");
        intent.putExtra("for_sell", forSell + "");
        intent.putExtra("for_rent", forRent + "");
        intent.putExtra("from_where", "item");

        startActivity(intent);
        this.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/


}
