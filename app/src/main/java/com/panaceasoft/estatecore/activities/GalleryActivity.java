package com.panaceasoft.estatecore.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.panaceasoft.estatecore.Config;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.models.PImageData;
import com.panaceasoft.estatecore.models.PItemData;
import com.panaceasoft.estatecore.models.PNewsData;
import com.panaceasoft.estatecore.uis.ExtendedViewPager;
import com.panaceasoft.estatecore.uis.TouchImageView;
import com.panaceasoft.estatecore.utilities.BitmapTransform;
import com.panaceasoft.estatecore.utilities.Utils;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class GalleryActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private TextView txtImgDesc;
    private static PNewsData newsData;
    private static PItemData itemData;
    private Bundle bundle;
    private String from;
    private static ArrayList<PImageData> imageArray;
    private TouchImageView imgView;
    private Picasso p;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initData();

        initUI();


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    class TouchImageAdapter extends PagerAdapter {

        Context context;
        Picasso p;

        private TouchImageAdapter(Picasso p){
            this.p = p;
        }

        @Override
        public int getCount() {
            if(imageArray != null) {
                return imageArray.size();
            }

            return 0;
        }


        @Override
        public View instantiateItem(ViewGroup container, int position) {
            imgView = new TouchImageView(container.getContext());
            if(imageArray != null) {
                if (position >= imageArray.size()) {
                    position = position % imageArray.size();
                }
                int MAX_WIDTH = Utils.getScreenWidth();
                int MAX_HEIGHT = MAX_WIDTH;
                p.load(Config.APP_IMAGES_URL + imageArray.get(position).path).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT)).placeholder(R.drawable.ps_icon)
                        .into(imgView);
                //txtImgDesc.setText(imageArray.get(position).description);
                container.addView(imgView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            }

            return imgView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
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

            p =new Picasso.Builder(this)
                    .memoryCache(new LruCache(1))
                    .build();

            ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
            mViewPager.setAdapter(new TouchImageAdapter(p));
            //mViewPager.setCurrentItem((Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % 12);
            txtImgDesc = (TextView) findViewById(R.id.img_desc);

            if(imageArray != null && imageArray.size() > 0) {
                txtImgDesc.setText(imageArray.get(0).description);
            }else{
                txtImgDesc.setText("");
            }
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                public void onPageScrollStateChanged(int arg0) {

                }

                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                public void onPageSelected(int currentPage) {

                    if (imageArray != null) {
                        if (currentPage >= imageArray.size()) {
                            currentPage = currentPage % imageArray.size();
                        }

                        //currentPage is the position that is currently displayed.
                        txtImgDesc.setText(imageArray.get(currentPage).description);
                    }
                }
            });
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
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
            bundle = getIntent().getBundleExtra("images_bundle");
            from =bundle.getString("from");

            if(from != null && from.equals("item")) {
                itemData = bundle.getParcelable("images");
                imageArray = itemData.images;
            } else {
                newsData = bundle.getParcelable("images");
                imageArray = newsData.images;
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

}
