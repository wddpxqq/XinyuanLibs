package com.xinyuan522.lib.wigdet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xinyuan522.library.R;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BannerView extends FrameLayout {

    private static final String TAG = "BannerView";

    private RecyclerView recyclerView;

    private Indicator mIndicator;

    private boolean isRecycler;

    private PagerSnapHelper mSnapHelper = new PagerSnapHelper();

    private BannerAdapter mAdapter = null;

    private ScheduledExecutorService mScheduledExecutorService  = null;

    private LinearLayoutManager mLayoutManager;


    // 轮播间隔
    private long carouselPeriod = 2000;

    // 轮播延迟开始时间
    private long carouselDelay = 2000;

    // 选中指示器
    private Drawable selectedDrawable;

    // 未选中指示器
    private Drawable unSelectedDrawable;

    private float  indicatorSpace;

    private float indicatorBottom;

    private ItemViewClickListener itemViewClickListener;

    public BannerView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        this.selectedDrawable = typedArray.getDrawable(R.styleable.BannerView_selectedIndicatorDrawable);
        this.unSelectedDrawable = typedArray.getDrawable(R.styleable.BannerView_unSelectedIndicatorDrawable);
        this.indicatorSpace = typedArray.getDimension(R.styleable.BannerView_indicatorSpace, 20f);
        this.indicatorBottom = typedArray.getDimension(R.styleable.BannerView_indicatorBottom, 20f);
        this.isRecycler = typedArray.getBoolean(R.styleable.BannerView_recycler, false);
        typedArray.recycle();

        recyclerView = new RecyclerView(context);
        mIndicator = new Indicator(context,attrs);

        LayoutParams layoutParamsForRecyclerView = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
        recyclerView.setLayoutParams(layoutParamsForRecyclerView);

        LayoutParams layoutParamsForIndicator = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
        mIndicator.setLayoutParams(layoutParamsForIndicator);

        addView(recyclerView);
        addView(mIndicator);

        initRecyclerView(context);
    }

    public void setOnItemViewClickListener(ItemViewClickListener listener){
        this.itemViewClickListener = listener;
    }

    private void initRecyclerView(Context context) {
        mLayoutManager = new SmoothLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new BannerAdapter(getContext(), null);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        mSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(onScrollListener);
    }

    // 监听滚动，获取指示器位置
    ///TODO 如果连续滑动 RecyclerView不会停止下来，那么切换下一张后，仍然不会走if。导致快速滑动时指示点位置不对的问题
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if( mAdapter.mData!=null){
                    int dataSize = mAdapter.mData.size();
                    int indicatorPosition = 0;
                    if(dataSize != 0){
                        indicatorPosition = mLayoutManager.findFirstVisibleItemPosition() % dataSize;
                    }
                    //得到指示器红点的位置
                    mIndicator.setPosition(indicatorPosition);
                }
            }else {

            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    } ;

    /**
     * 设置banner数据
     * banner数据设置一次会全部更新
     * 建议只设置一次
     * @param mData
     */
    public void setData(List mData) {
        if (mData != null) {
            mAdapter.mData = mData;
            mAdapter.notifyDataSetChanged();
            mIndicator.setNumber(mData.size());
        }
    }

    /**
     * 启动轮播
     * @param layoutManager
     */
    private void startCarouse(final LinearLayoutManager layoutManager) {
        if (mScheduledExecutorService == null) {
            int poolSize = 1;
            mScheduledExecutorService = Executors.newScheduledThreadPool(poolSize);
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(layoutManager.findFirstVisibleItemPosition() + 1);
            }
        };
        mScheduledExecutorService.scheduleAtFixedRate(runnable, carouselDelay, carouselPeriod, TimeUnit.MILLISECONDS);
    }


    private class SmoothLinearLayoutManager extends LinearLayoutManager {

        private LinearSmoothScroller linearSmoothScroller;

        public SmoothLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            linearSmoothScroller  = new LinearSmoothScroller(context){
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    // 返回0.2
                    return 0.2f;
                }
            };
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            super.smoothScrollToPosition(recyclerView, state, position);
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }

    /**
     * 滚动指示器
     */
    private final class Indicator extends View {

        private int number;
        private int position = 0;

        public Indicator(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            int vWidth = getWidth();
            int vHeight = getHeight();
            int radius = selectedDrawable.getIntrinsicWidth();
            int iWidth = (int) (radius * number + indicatorSpace * (number - 1));
            int left = (vWidth - iWidth) / 2;
            int right = left + radius;
            int top = (int) (vHeight - indicatorBottom - radius);
            int bottom = (int) (vHeight - indicatorBottom);
            Rect bounds = new Rect(left, top, right, bottom);
            canvas.save();
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
            for (int i = 0; i < number; i++) {
                if (i == position) {
                    getIndicatorRect(i,selectedDrawable,bounds);
                    selectedDrawable.setBounds(bounds);
                    selectedDrawable.draw(canvas);
                } else {
                    bounds.left = (int) (left + ( radius + indicatorSpace) * i);
                    bounds.right = bounds.left + radius;
                    unSelectedDrawable.setBounds(bounds);
                    unSelectedDrawable.draw(canvas);
                }
            }
            canvas.restore();
        }

        private Rect getIndicatorRect(int i, Drawable drawable, Rect bounds) {
            int radius = drawable.getIntrinsicWidth();
            bounds.left = (int) (bounds.left + ( radius + indicatorSpace) * i);
            bounds.right = bounds.left + radius;
            return bounds;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public void setPosition(int position) {
            this.position = position;
            invalidate();
        }
    }

    private final class BannerAdapter<T> extends RecyclerView.Adapter<BannerViewHolder> {

        private List<T> mData;
        private Context context;
        private int resId;

        public BannerAdapter(Context context, List<T> list) {
            this.mData = list;
            this.context = context;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            ImageView imageView = new ImageView(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return new BannerViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            int dataSize = mData.size();
            int index = dataSize == 0 ? 0 : (position % dataSize);
            T data = mData.get(index);
            Glide.with(context).load(data).into(holder.imageView);
            if (itemViewClickListener != null) {
                holder.imageView.setOnClickListener(view -> itemViewClickListener.onItemViewClick(position, data));
            }
        }

        @Override
        public int getItemCount() {
            if(BannerView.this.isRecycler){
                // 轮播图是无限轮播
                return mData != null ? Integer.MAX_VALUE: 0;
            }else {
                return mData != null ? mData.size(): 0;
            }
        }
    }

    private final class BannerViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public BannerViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view;
        }
    }

    public interface ItemViewClickListener<T>{
        void onItemViewClick(int position, T data);
    }
}
