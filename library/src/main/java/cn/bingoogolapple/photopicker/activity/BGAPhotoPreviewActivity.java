package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ablingbling.library.photoview.PhotoViewAttacher;

import java.util.ArrayList;

import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPageAdapter;
import cn.bingoogolapple.photopicker.common.BGAKey;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;
import qiu.niorgai.StatusBarCompat;

public class BGAPhotoPreviewActivity extends AppCompatActivity implements PhotoViewAttacher.OnViewTapListener {

    private FrameLayout frame_container;
    private Toolbar toolbar;
    private BGAHackyViewPager viewPager;
    private TextView tv_title;
    private AppCompatImageView iv_delete;

    private int mDeleteIcon;
    private int mActionBarColor;
    private int mActionBarTextColor;
    private int mBackResId;
    private int mBackgroundColor;
    private boolean mIsHidden = false;
    private int mCurrentPosition;
    private ArrayList<String> mSelectedPhotos;

    private BGAPhotoPageAdapter mPhotoPageAdapter;

    /**
     * 上一次标题栏显示或隐藏的时间戳
     */
    private long mLastShowHiddenTime;

    public static class IntentBuilder {

        private Intent mIntent;

        public IntentBuilder(Context context) {
            mIntent = new Intent(context, BGAPhotoPreviewActivity.class);
        }

        public IntentBuilder deleteIcon(int icon) {
            mIntent.putExtra(BGAKey.EXTRA_DELETE_ICON, icon);
            return this;
        }

        public IntentBuilder actionBarColor(int color) {
            mIntent.putExtra(BGAKey.EXTRA_ACTIONBAR_COLOR, color);
            return this;
        }

        public IntentBuilder actionBarTextColor(int color) {
            mIntent.putExtra(BGAKey.EXTRA_ACTIONBAR_TEXT_COLOR, color);
            return this;
        }

        public IntentBuilder backResId(int backResId) {
            mIntent.putExtra(BGAKey.EXTRA_BACK_RESID, backResId);
            return this;
        }

        public IntentBuilder backgroundColor(int color) {
            mIntent.putExtra(BGAKey.EXTRA_BACKGROUND_COLOR, color);
            return this;
        }

        public IntentBuilder isHidden(boolean isHidden) {
            mIntent.putExtra(BGAKey.EXTRA_IS_HIDDEN, isHidden);
            return this;
        }

        /**
         * 当前预览的图片路径集合
         */
        public IntentBuilder selectedPhotos(ArrayList<String> photos) {
            mIntent.putStringArrayListExtra(BGAKey.EXTRA_SELECTED_PHOTOS, photos);
            return this;
        }

        /**
         * 当前预览的图片索引
         */
        public IntentBuilder currentPosition(int currentPosition) {
            mIntent.putExtra(BGAKey.EXTRA_CURRENT_POSITION, currentPosition);
            return this;
        }

        public Intent build() {
            return mIntent;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bga_activity_photo_preview);
        initData();
        initView();
        setView();
    }

    private void initData() {
        mDeleteIcon = getIntent().getIntExtra(BGAKey.EXTRA_DELETE_ICON, R.drawable.bga_ic_delete);
        mActionBarColor = getIntent().getIntExtra(BGAKey.EXTRA_ACTIONBAR_COLOR, Color.parseColor("#000000"));
        mActionBarTextColor = getIntent().getIntExtra(BGAKey.EXTRA_ACTIONBAR_TEXT_COLOR, Color.parseColor("#ffffff"));
        mBackResId = getIntent().getIntExtra(BGAKey.EXTRA_BACK_RESID, R.mipmap.bga_app_ic_back);
        mBackgroundColor = getIntent().getIntExtra(BGAKey.EXTRA_BACKGROUND_COLOR, Color.parseColor("#000000"));
        mIsHidden = getIntent().getBooleanExtra(BGAKey.EXTRA_IS_HIDDEN, true);
        mCurrentPosition = getIntent().getIntExtra(BGAKey.EXTRA_CURRENT_POSITION, 0);
        mSelectedPhotos = getIntent().getStringArrayListExtra(BGAKey.EXTRA_SELECTED_PHOTOS);
        if (mSelectedPhotos == null) {
            mSelectedPhotos = new ArrayList<>();
        }
    }

    private void initView() {
        frame_container = findViewById(R.id.frame_container);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);

        StatusBarCompat.setStatusBarColor(this, mActionBarColor);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setBackgroundColor(mActionBarColor);
        toolbar.setNavigationIcon(mBackResId);

        frame_container.setBackgroundColor(mBackgroundColor);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                renderTitleTv();
            }

        });
    }

    private void setView() {
        mPhotoPageAdapter = new BGAPhotoPageAdapter(this, mSelectedPhotos);
        viewPager.setAdapter(mPhotoPageAdapter);
        viewPager.setCurrentItem(mCurrentPosition, false);

        //过2秒隐藏标题栏
        toolbar.postDelayed(new Runnable() {

            @Override
            public void run() {
                hiddenTitleBar();
            }

        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_menu_photo_preview, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_preview_title);
        View actionView = menuItem.getActionView();

        tv_title = actionView.findViewById(R.id.tv_title);
        iv_delete = actionView.findViewById(R.id.iv_delete);

        tv_title.setTextColor(mActionBarTextColor);
        renderTitleTv();

        iv_delete.setImageResource(mDeleteIcon);
        iv_delete.setOnClickListener(new BGAOnNoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                mPhotoPageAdapter.remove(viewPager.getCurrentItem());

                renderTitleTv();
            }

        });

        return true;
    }

    private void renderTitleTv() {
        if (tv_title == null || mPhotoPageAdapter == null) {
            return;
        }

        tv_title.setText((viewPager.getCurrentItem() + 1) + "/" + mPhotoPageAdapter.getCount());
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (System.currentTimeMillis() - mLastShowHiddenTime > 500) {
            mLastShowHiddenTime = System.currentTimeMillis();

            if (mIsHidden) {
                showTitleBar();

            } else {
                hiddenTitleBar();
            }
        }
    }

    private void showTitleBar() {
        if (toolbar != null) {
            ViewCompat.animate(toolbar).translationY(0).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = false;
                }

            }).start();
        }
    }

    private void hiddenTitleBar() {
        if (toolbar != null) {
            ViewCompat.animate(toolbar).translationY(-toolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = true;
                }

            }).start();
        }
    }

}