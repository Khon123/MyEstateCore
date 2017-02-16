package com.panaceasoft.estatecore.uis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.utilities.Utils;


/**
 * Created by Panacea-Soft on 10/12/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class PSNumberSelector extends LinearLayout {

    public int VALUE = 0;
    private int LIMIT = 0;
    private String TITLE = "";

    private ImageButton btnMinus;
    private ImageButton btnPlus;
    private TextView txtCount;
    private TextView txtTitle;


    public PSNumberSelector(Context context, String title, int limit) {
        super(context);

        this.LIMIT = limit;
        this.VALUE = 0;
        this.TITLE = title;

        initUI(context);
    }

    /**
     * Inflate the UI for the layout
     *
     * @param context for the view
     */
    private void initUI(Context context) {
        Utils.psLog("initUI" + context.toString());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ui_ps_number_selector, this);
        onFinishInflateCustom();
    }

    protected void onFinishInflateCustom() {
        super.onFinishInflate();

        txtCount = (TextView) findViewById(R.id.txt_count);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        btnMinus = (ImageButton) findViewById(R.id.btn_minus);
        btnPlus = (ImageButton) findViewById(R.id.btn_plus);

        btnMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VALUE != 0) {
                    VALUE--;

                    txtCount.setText(VALUE + "");

                    updateBtnColor();
                }
            }
        });

        btnPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VALUE < LIMIT) {
                    VALUE++;

                    txtCount.setText(VALUE + "");

                    updateBtnColor();
                }
            }
        });

        updateTitle();
        updateCount();
        updateBtnColor();

    }

    private void updateCount() {
        txtCount.setText("0");
    }

    private void updateTitle() {
        txtTitle.setText(this.TITLE);
    }

    private void updateBtnColor(){
        updateMinusColor();
        updatePlusColor();
    }

    private void updateMinusColor(){
        if(VALUE == 0) {
            btnMinus.setColorFilter(getResources().getColor(R.color.colorDivider));
        }else{
            btnMinus.setColorFilter(getResources().getColor(R.color.colorAccent));
        }
    }

    private void updatePlusColor(){
        if(VALUE == LIMIT) {
            btnPlus.setColorFilter(getResources().getColor(R.color.colorDivider));
        }else{
            btnPlus.setColorFilter(getResources().getColor(R.color.colorAccent));
        }
    }

    public int getValue(){
        return this.VALUE;
    }
}
