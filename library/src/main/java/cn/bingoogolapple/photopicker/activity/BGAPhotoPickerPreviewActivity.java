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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;
import qiu.niorgai.StatusBarCompat;

public class BGAPhotoPickerPreviewActivity extends AppCompatActivity implements PhotoViewAttacher.OnViewTapListener {

    private FrameLayout frame_container;
    private Toolbar toolbar;
    private BGAHackyViewPager viewPager;
    private FrameLayout frame_choose;
    private TextView tv_choose;
    private TextView tv_title;
    private TextView tv_submit;

    private String mSubmit;
    private int mActionBarColor;
    private int mActionBarTextColor;
    private int mBackResId;
    private int mBackgroundColor;
    private int mMaxChooseCount;//最多选择多少张图片，默认等于1，为单选
    private ArrayList<String> mSelectedPhotos;
    private ArrayList<String> mPreviewPhotos;
    private boolean mIsHidden;
    private long mLastShowHiddenTime;//上一次标题栏显示或隐藏的时间戳
    private boolean mIsFromTakePhoto;//是否是拍完照后跳转过来
    private int mCurrentPosition;

    private BGAPhotoPageAdapter mPhotoPageAdapter;

    public static class IntentBuilder {

        private Intent mIntent;

        public IntentBuilder(Context context) {
            mIntent = new Intent(context, BGAPhotoPickerPreviewActivity.class);
        }

        public IntentBuilder submit(String submit) {
            mIntent.putExtra(BGAKey.EXTRA_SUBMIT, submit);
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
         * 图片选择张数的最大值
         */
        public IntentBuilder maxChooseCount(int count) {
            mIntent.putExtra(BGAKey.EXTRA_MAX_CHOOSE_COUNT, count);
            return this;
        }

        /**
         * 当前已选中的图片路径集合
         */
        public IntentBuilder selectedPhotos(ArrayList<String> photos) {
            mIntent.putStringArrayListExtra(BGAKey.EXTRA_SELECTED_PHOTOS, photos);
            return this;
        }

        /**
         * 当前预览的图片路径集合
         */
        public IntentBuilder previewPhotos(ArrayList<String> photos) {
            mIntent.putStringArrayListExtra(BGAKey.EXTRA_PREVIEW_PHOTOS, photos);
            return this;
        }

        /**
         * 当前预览图片的索引
         */
        public IntentBuilder currentPosition(int position) {
            mIntent.putExtra(BGAKey.EXTRA_CURRENT_POSITION, position);
            return this;
        }

        /**
         * 是否是拍完照后跳转过来
         */
        public IntentBuilder isFromTakePhoto(boolean isFromTakePhoto) {
            mIntent.putExtra(BGAKey.EXTRA_IS_FROM_TAKE_PHOTO, isFromTakePhoto);
            return this;
        }

        public Intent build() {
            return mIntent;
        }
    }

    /**
     * 获取已选择的图片集合
     */
    public static ArrayList<String> getSelectedPhotos(Intent intent) {
        return intent.getStringArrayListExtra(BGAKey.EXTRA_SELECTED_PHOTOS);
    }

    /**
     * 是否是拍照预览
     */
    public static boolean getIsFromTakePhoto(Intent intent) {
        return intent.getBooleanExtra(BGAKey.EXTRA_IS_FROM_TAKE_PHOTO, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bga_activity_photo_picker_preview);
        initData();
        initView();
        setView();
    }

    private void initData() {
        mSubmit = getIntent().getStringExtra(BGAKey.EXTRA_SUBMIT);
        if (mSubmit == null) {
            mSubmit = "确定";
        }

        mActionBarColor = getIntent().getIntExtra(BGAKey.EXTRA_ACTIONBAR_COLOR, Color.parseColor("#000000"));
        mActionBarTextColor = getIntent().getIntExtra(BGAKey.EXTRA_ACTIONBAR_TEXT_COLOR, Color.parseColor("#ffffff"));
        mBackResId = getIntent().getIntExtra(BGAKey.EXTRA_BACK_RESID, R.mipmap.bga_app_ic_back);
        mBackgroundColor = getIntent().getIntExtra(BGAKey.EXTRA_BACKGROUND_COLOR, Color.parseColor("#000000"));
        mMaxChooseCount = getIntent().getIntExtra(BGAKey.EXTRA_MAX_CHOOSE_COUNT, 1);

        mSelectedPhotos = getIntent().getStringArrayListExtra(BGAKey.EXTRA_SELECTED_PHOTOS);
        if (mSelectedPhotos == null) {
            mSelectedPhotos = new ArrayList<>();
        }

        mPreviewPhotos = getIntent().getStringArrayListExtra(BGAKey.EXTRA_PREVIEW_PHOTOS);
        if (TextUtils.isEmpty(mPreviewPhotos.get(0))) {
            mPreviewPhotos.remove(0);
        }

        mIsHidden = getIntent().getBooleanExtra(BGAKey.EXTRA_IS_HIDDEN, true);

        // 处理是否是拍完照后跳转过来
        mIsFromTakePhoto = getIntent().getBooleanExtra(BGAKey.EXTRA_IS_FROM_TAKE_PHOTO, false);

        mCurrentPosition = getIntent().getIntExtra(BGAKey.EXTRA_CURRENT_POSITION, 0);
    }

    private void initView() {
        frame_container = findViewById(R.id.frame_container);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        frame_choose = findViewById(R.id.frame_choose);
        tv_choose = findViewById(R.id.tv_choose);

        StatusBarCompat.setStatusBarColor(this, mActionBarColor);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setBackgroundColor(mActionBarColor);
        toolbar.setNavigationIcon(mBackResId);

        frame_container.setBackgroundColor(mBackgroundColor);
        frame_choose.setBackgroundColor(mActionBarColor);
        tv_choose.setTextColor(mActionBarTextColor);

        tv_choose.setOnClickListener(new BGAOnNoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                String currentPhoto = mPhotoPageAdapter.getItem(viewPager.getCurrentItem());
                if (mSelectedPhotos.contains(currentPhoto)) {
                    mSelectedPhotos.remove(currentPhoto);
                    tv_choose.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_normal, 0, 0, 0);
                    renderTopRightBtn();

                } else {
                    if (mMaxChooseCount == 1) {// 单选
                        mSelectedPhotos.clear();
                        mSelectedPhotos.add(currentPhoto);
                        tv_choose.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);
                        renderTopRightBtn();

                    } else {// 多选
                        if (mMaxChooseCount == mSelectedPhotos.size()) {
                            BGAPhotoPickerUtil.show(getString(R.string.bga_pp_toast_photo_picker_max, mMaxChooseCount));

                        } else {
                            mSelectedPhotos.add(currentPhoto);
                            tv_choose.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);
                            renderTopRightBtn();
                        }
                    }
                }
            }

        });

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                handlePageSelectedStatus();
            }

        });
    }

    private void setView() {
        frame_choose.setVisibility(mIsFromTakePhoto ? View.GONE : View.VISIBLE);

        mPhotoPageAdapter = new BGAPhotoPageAdapter(this, mPreviewPhotos);
        viewPager.setAdapter(mPhotoPageAdapter);
        viewPager.setCurrentItem(mCurrentPosition);

        // 过2秒隐藏标题栏和底部选择栏
        toolbar.postDelayed(new Runnable() {

            @Override
            public void run() {
                hiddenToolBarAndChooseBar();
            }

        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_menu_photo_picker_preview, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_picker_preview_title);
        View actionView = menuItem.getActionView();

        tv_title = actionView.findViewById(R.id.bga_tv_title);
        tv_submit = actionView.findViewById(R.id.bga_tv_submit);

        tv_title.setTextColor(mActionBarTextColor);
        tv_submit.setTextColor(mActionBarTextColor);

        tv_submit.setOnClickListener(new BGAOnNoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(BGAKey.EXTRA_SELECTED_PHOTOS, mSelectedPhotos);
                intent.putExtra(BGAKey.EXTRA_IS_FROM_TAKE_PHOTO, mIsFromTakePhoto);
                setResult(RESULT_OK, intent);
                finish();
            }

        });

        renderTopRightBtn();
        handlePageSelectedStatus();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(BGAKey.EXTRA_SELECTED_PHOTOS, mSelectedPhotos);
        intent.putExtra(BGAKey.EXTRA_IS_FROM_TAKE_PHOTO, mIsFromTakePhoto);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void handlePageSelectedStatus() {
        if (tv_title == null || mPhotoPageAdapter == null) {
            return;
        }

        tv_title.setText((viewPager.getCurrentItem() + 1) + "/" + mPhotoPageAdapter.getCount());

        if (mSelectedPhotos.contains(mPhotoPageAdapter.getItem(viewPager.getCurrentItem()))) {
            tv_choose.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);

        } else {
            tv_choose.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_normal, 0, 0, 0);
        }
    }

    /**
     * 渲染右上角按钮
     */
    private void renderTopRightBtn() {
        if (mIsFromTakePhoto) {
            tv_submit.setEnabled(true);
            tv_submit.setText(mSubmit);

        } else if (mSelectedPhotos.size() == 0) {
            tv_submit.setEnabled(false);
            tv_submit.setText(mSubmit);

        } else {
            tv_submit.setEnabled(true);
            tv_submit.setText(mSubmit + "(" + mSelectedPhotos.size() + "/" + mMaxChooseCount + ")");
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (System.currentTimeMillis() - mLastShowHiddenTime > 500) {
            mLastShowHiddenTime = System.currentTimeMillis();

            if (mIsHidden) {
                showTitleBarAndChooseBar();

            } else {
                hiddenToolBarAndChooseBar();
            }
        }
    }

    private void showTitleBarAndChooseBar() {
        if (toolbar != null) {
            ViewCompat.animate(toolbar).translationY(0).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = false;
                }

            }).start();
        }

        if (!mIsFromTakePhoto && frame_choose != null) {
            frame_choose.setVisibility(View.VISIBLE);
            frame_choose.setAlpha(0);
            ViewCompat.animate(frame_choose).alpha(1).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    private void hiddenToolBarAndChooseBar() {
        if (toolbar != null) {
            ViewCompat.animate(toolbar).translationY(-toolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = true;

                    if (frame_choose != null) {
                        frame_choose.setVisibility(View.INVISIBLE);
                    }
                }

            }).start();
        }

        if (!mIsFromTakePhoto) {
            if (frame_choose != null) {
                ViewCompat.animate(frame_choose).alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        }
    }

}