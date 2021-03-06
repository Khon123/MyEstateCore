package com.panaceasoft.estatecore.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.panaceasoft.estatecore.Config;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.models.CategoryRowData;
import com.panaceasoft.estatecore.utilities.BitmapTransform;
import com.panaceasoft.estatecore.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Panacea-Soft on 17/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CategoryRowData> mDataset;

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Picasso p;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean isFooterEnabled = true;


    private  class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        }
    }

    public CategoryAdapter(final List<CategoryRowData> myDataSet, RecyclerView recyclerView, Picasso p) {
        mDataset = myDataSet;

        this.p = p;

        if(recyclerView.getLayoutManager()instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            //onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }else if(recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = gridLayoutManager.getItemCount();
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });

        }else if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(myDataSet != null && myDataSet.size() > 0) {

                        totalItemCount = staggeredGridLayoutManager.getItemCount();

                        // for staggeredGridLayoutManager
                        int[] arr = new int[totalItemCount];
                        int[] lastVisibleItem2 = staggeredGridLayoutManager.findLastVisibleItemPositions(arr);
                        String string = "";
                        int greatestItem = 0;
                        for (int i = 0; i < lastVisibleItem2.length; i++) {
                            if (lastVisibleItem2[i] > greatestItem) {
                                greatestItem = lastVisibleItem2[i];
                            }
                            string += " = " + lastVisibleItem2[i];
                        }
                        if (!loading && totalItemCount <= (greatestItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position)!=null? VIEW_ITEM: VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder vh;

            if (viewType == VIEW_ITEM) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_row, parent, false);

                vh = new MyViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.progress_item, parent, false);

                vh = new ProgressViewHolder(v);
            }

        return vh;
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(mDataset != null && mDataset.size() > 0) {
            if (holder instanceof MyViewHolder) {
                int MAX_WIDTH = Utils.getScreenWidth()/2;
                int MAX_HEIGHT = MAX_WIDTH;
                ((MyViewHolder) holder).title.setText(mDataset.get(position).getCatName());
                Utils.psLog(" Half Of Screen = " + MAX_WIDTH);


                ((MyViewHolder) holder).icon.destroyDrawingCache();

                //p.with(((MyViewHolder)holder)
                //.icon.getContext())
                p.load(Config.APP_IMAGES_URL + mDataset.get(position).getCatImage())
                        .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                        .placeholder(R.drawable.ps_icon)
                        .into(((MyViewHolder) holder).icon);
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }
    }

    public void clearData() {

        if(this.mDataset != null) {
            int size = this.mDataset.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.mDataset.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }
    }

    public void setLoaded(){
        loading = false;
    }

    @Override
    public int getItemCount() {
        if(mDataset != null) {
            return mDataset.size();
        }
        return 0;
    }

    private interface OnLoadMoreListener{
        void onLoadMore();
    }

    private static final class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView icon;
        private MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.category_name);
            icon = (ImageView) itemView.findViewById(R.id.category_image);
            title.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        }
    }
}


