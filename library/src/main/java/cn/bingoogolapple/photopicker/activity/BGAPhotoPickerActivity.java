package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPickerAdapter;
import cn.bingoogolapple.photopicker.common.BGAKey;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import cn.bingoogolapple.photopicker.util.BGALoadPhotoTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.util.DensityUtil;
import cn.bingoogolapple.photopicker.widget.GridSpacingItemDecoration;
import qiu.niorgai.StatusBarCompat;

public class BGAPhotoPickerActivity extends AppCompatActivity implements BGAOnItemChildClickListener, BGAAsyncTask.Callback<List<BGAPhotoFolderModel>> {

    private static final String STATE_SELECTED_PHOTOS = "STATE_SELECTED_PHOTOS";

    private static final int REQUEST_TAKE_PHOTO = 1;//拍照的请求码
    private static final int REQUEST_PREVIEW = 2;//预览照片的请求码

    private LinearLayout linear_container;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tv_title;
    private ImageView iv_titleArrow;
    private TextView tv_submit;

    private AppCompatDialog mLoadingDialog;
    private BGAPhotoFolderModel mCurrentPhotoFolderModel;
    private BGAPhotoPickerAdapter mPicAdapter;
    private BGAPhotoFolderPw mPhotoFolderPw;
    private BGALoadPhotoTask mLoadPhotoTask;

    private String mTitle;
    private String mSubmit;
    private String mPreviewSubmit;
    private boolean mPreviewIsHidden;
    private int mActionBarColor;
    private int mActionBarTextColor;
    private int mBackResId;
    private int mBackgroundColor;
    private int mMaxChooseCount;//最多选择多少张图片，默认等于1，为单选
    private BGAPhotoHelper mPhotoHelper;
    private boolean mPauseOnScroll;
    private int mSpanCount;
    private int mGridSpace;
    private boolean mTakePhotoEnabled;//是否可以拍照
    private List<BGAPhotoFolderModel> mPhotoFolderModels;//图片目录数据集合
    private List<String> mSelectedPhotos;

    private BGAOnNoDoubleClickListener mOnClickShowPhotoFolderListener = new BGAOnNoDoubleClickListener() {

        @Override
        public void onNoDoubleClick(View v) {
            if (mPhotoFolderModels != null && mPhotoFolderModels.size() > 0) {
                showPhotoFolderPw();
            }
        }

    };

    public static class IntentBuilder {

        private Intent mIntent;

        public IntentBuilder(Context context) {
            mIntent = new Intent(context, BGAPhotoPickerActivity.class);
        }

        public IntentBuilder title(String title) {
            mIntent.putExtra(BGAKey.EXTRA_TITLE, title);
            return this;
        }

        public IntentBuilder submit(String submit) {
            mIntent.putExtra(BGAKey.EXTRA_SUBMIT, submit);
            return this;
        }

        public IntentBuilder previewSubmit(String submit) {
            mIntent.putExtra(BGAKey.EXTRA_PREVIEW_SUBMIT, submit);
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

        public IntentBuilder previewIsHidden(boolean previewIsHidden) {
            mIntent.putExtra(BGAKey.EXTRA_PREVIEW_IS_HIDDEN, previewIsHidden);
            return this;
        }

        /**
         * 拍照后图片保存的目录。如果传 null 表示没有拍照功能，如果不为 null 则具有拍照功能，
         */
        public IntentBuilder cameraFileDir(@Nullable File cameraFileDir) {
            mIntent.putExtra(BGAKey.EXTRA_CAMERA_FILE_DIR, cameraFileDir);
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
         * 当前已选中的图片路径集合，可以传 null
         */
        public IntentBuilder selectedPhotos(@Nullable List<String> selectedPhotos) {
            mIntent.putExtra(BGAKey.EXTRA_SELECTED_PHOTOS, (Serializable) selectedPhotos);
            return this;
        }

        /**
         * 滚动列表时是否暂停加载图片，默认为 false
         */
        public IntentBuilder pauseOnScroll(boolean pauseOnScroll) {
            mIntent.putExtra(BGAKey.EXTRA_PAUSE_ON_SCROLL, pauseOnScroll);
            return this;
        }

        /**
         * 最多列数
         */
        public IntentBuilder spanCount(int count) {
            mIntent.putExtra(BGAKey.EXTRA_SPAN_COUNT, count);
            return this;
        }

        public IntentBuilder gridSpace(int space) {
            mIntent.putExtra(BGAKey.EXTRA_GRID_SPACE, space);
            return this;
        }

        public Intent build() {
            return mIntent;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bga_activity_photo_picker);
        initData();
        initView();
        setView();
    }

    private void initData() {
        mTitle = getIntent().getStringExtra(BGAKey.EXTRA_TITLE);
        if (mTitle == null) {
            mTitle = "所有图片";
        }

        mSubmit = getIntent().getStringExtra(BGAKey.EXTRA_SUBMIT);
        if (mSubmit == null) {
            mSubmit = "完成";
        }

        mPreviewSubmit = getIntent().getStringExtra(BGAKey.EXTRA_PREVIEW_SUBMIT);

        mPreviewIsHidden = getIntent().getBooleanExtra(BGAKey.EXTRA_PREVIEW_IS_HIDDEN, true);

        mActionBarColor = getIntent().getIntExtra(BGAKey.EXTRA_ACTIONBAR_COLOR, Color.parseColor("#000000"));

        mActionBarTextColor = getIntent().getIntExtra(BGAKey.EXTRA_ACTIONBAR_TEXT_COLOR, Color.parseColor("#ffffff"));

        mBackResId = getIntent().getIntExtra(BGAKey.EXTRA_BACK_RESID, R.mipmap.bga_app_ic_back);

        mBackgroundColor = getIntent().getIntExtra(BGAKey.EXTRA_BACKGROUND_COLOR, Color.parseColor("#000000"));

        //获取拍照图片保存目录
        File cameraFileDir = (File) getIntent().getSerializableExtra(BGAKey.EXTRA_CAMERA_FILE_DIR);
        if (cameraFileDir != null) {
            mPhotoHelper = new BGAPhotoHelper(cameraFileDir);
            mTakePhotoEnabled = true;

        } else {
            mPhotoHelper = null;
            mTakePhotoEnabled = false;
        }

        mMaxChooseCount = getIntent().getIntExtra(BGAKey.EXTRA_MAX_CHOOSE_COUNT, 1);
        mPauseOnScroll = getIntent().getBooleanExtra(BGAKey.EXTRA_PAUSE_ON_SCROLL, false);
        mSpanCount = getIntent().getIntExtra(BGAKey.EXTRA_SPAN_COUNT, 4);
        mGridSpace = getIntent().getIntExtra(BGAKey.EXTRA_GRID_SPACE, DensityUtil.dp2px(2));

        mSelectedPhotos = (List<String>) getIntent().getSerializableExtra(BGAKey.EXTRA_SELECTED_PHOTOS);
        if (mSelectedPhotos == null) {
            mSelectedPhotos = new ArrayList<>();

        } else if (mSelectedPhotos.size() > mMaxChooseCount) {
            mSelectedPhotos.clear();
        }
    }

    private void initView() {
        linear_container = findViewById(R.id.linear_container);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);

        StatusBarCompat.setStatusBarColor(this, mActionBarColor);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTitle);
        toolbar.setBackgroundColor(mActionBarColor);
        toolbar.setNavigationIcon(mBackResId);

        linear_container.setBackgroundColor(mBackgroundColor);

        mPicAdapter = new BGAPhotoPickerAdapter(recyclerView);
        mPicAdapter.setOnItemChildClickListener(this);

        if (mPauseOnScroll) {
            recyclerView.addOnScrollListener(new BGARVOnScrollListener(this));
        }

        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {

            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                BGAImage.clear(BGAPhotoPickerActivity.this, holder.itemView);
            }

        });
    }

    private void setView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, mSpanCount, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(layoutManager.getSpanCount(), mGridSpace, false));

        recyclerView.setAdapter(mPicAdapter);
        mPicAdapter.setSelectedPhotos(mSelectedPhotos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoadingDialog();
        mLoadPhotoTask = new BGALoadPhotoTask(this, this, mTakePhotoEnabled, mTitle).perform();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_menu_photo_picker, menu);
        MenuItem menuItem = menu.findItem(R.id.item_picker);
        View actionView = menuItem.getActionView();

        tv_title = actionView.findViewById(R.id.bga_tv_title);
        iv_titleArrow = actionView.findViewById(R.id.bga_iv_title_arrow);
        tv_submit = actionView.findViewById(R.id.bga_tv_submit);

        tv_title.setTextColor(mActionBarTextColor);
        iv_titleArrow.setColorFilter(mActionBarTextColor);
        tv_submit.setTextColor(mActionBarTextColor);

        tv_title.setOnClickListener(mOnClickShowPhotoFolderListener);
        iv_titleArrow.setOnClickListener(mOnClickShowPhotoFolderListener);
        tv_submit.setOnClickListener(new BGAOnNoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                returnSelectedPhotos(mPicAdapter.getSelectedPhotos());
            }

        });

        if (mCurrentPhotoFolderModel != null) {
            tv_title.setText(mCurrentPhotoFolderModel.name);

        } else {
            tv_title.setText(mTitle);
        }

        renderTopRightBtn();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new AppCompatDialog(this);
            mLoadingDialog.setContentView(R.layout.bga_pp_dialog_loading);
            mLoadingDialog.setCancelable(false);
        }

        mLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 返回已选中的图片集合
     */
    private void returnSelectedPhotos(List<String> selectedPhotos) {
        Intent intent = new Intent();
        intent.putExtra(BGAKey.EXTRA_SELECTED_PHOTOS, (Serializable) selectedPhotos);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showPhotoFolderPw() {
        if (iv_titleArrow == null) {
            return;
        }

        if (mPhotoFolderPw == null) {
            mPhotoFolderPw = new BGAPhotoFolderPw(this, toolbar, new BGAPhotoFolderPw.Delegate() {

                @Override
                public void onSelectedFolder(int position) {
                    reloadPhotos(position);
                }

                @Override
                public void executeDismissAnim() {
                    ViewCompat.animate(iv_titleArrow).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(0).start();
                }

            });
        }
        mPhotoFolderPw.setData(mPhotoFolderModels);
        mPhotoFolderPw.show();

        ViewCompat.animate(iv_titleArrow).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(-180).start();
    }

    /**
     * 显示只能选择 mMaxChooseCount 张图的提示
     */
    private void toastMaxCountTip() {
        BGAPhotoPickerUtil.show(getString(R.string.bga_pp_toast_photo_picker_max, mMaxChooseCount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                List<String> photos = Arrays.asList(mPhotoHelper.getCameraFilePath());

                Intent intent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                        .submit(mPreviewSubmit)
                        .actionBarColor(mActionBarColor)
                        .actionBarTextColor(mActionBarTextColor)
                        .backResId(mBackResId)
                        .backgroundColor(mBackgroundColor)
                        .isHidden(mPreviewIsHidden)
                        .isFromTakePhoto(true)
                        .maxChooseCount(1)
                        .previewPhotos(photos)
                        .selectedPhotos(photos)
                        .currentPosition(0)
                        .build();

                startActivityForResult(intent, REQUEST_PREVIEW);

            } else if (requestCode == REQUEST_PREVIEW) {
                if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {//从拍照预览界面返回，刷新图库
                    mPhotoHelper.refreshGallery();
                }

                returnSelectedPhotos((List<String>) data.getSerializableExtra(BGAKey.EXTRA_SELECTED_PHOTOS));
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == REQUEST_PREVIEW) {
            if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {//从拍照预览界面返回，删除之前拍的照片
                mPhotoHelper.deleteCameraFile();

            } else {
                mPicAdapter.setSelectedPhotos((List<String>) data.getSerializableExtra(BGAKey.EXTRA_SELECTED_PHOTOS));
                renderTopRightBtn();
            }
        }
    }

    /**
     * 渲染右上角按钮
     */
    private void renderTopRightBtn() {
        if (tv_submit == null) {
            return;
        }

        if (mPicAdapter.getSelectedCount() == 0) {
            tv_submit.setEnabled(false);
            tv_submit.setText(mSubmit);

        } else {
            tv_submit.setEnabled(true);
            tv_submit.setText(mSubmit + "(" + mPicAdapter.getSelectedCount() + "/" + mMaxChooseCount + ")");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BGAPhotoHelper.onSaveInstanceState(mPhotoHelper, outState);
        outState.putSerializable(STATE_SELECTED_PHOTOS, (Serializable) mPicAdapter.getSelectedPhotos());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BGAPhotoHelper.onRestoreInstanceState(mPhotoHelper, savedInstanceState);
        mPicAdapter.setSelectedPhotos((List<String>) savedInstanceState.getSerializable(STATE_SELECTED_PHOTOS));
    }

    @Override
    public void onItemChildClick(ViewGroup viewGroup, View view, int position) {
        if (view.getId() == R.id.iv_camera) {
            handleTakePhoto();

        } else if (view.getId() == R.id.iv_photo) {
            changeToPreview(position);

        } else if (view.getId() == R.id.iv_check) {
            handleClickSelectFlagIv(position);
        }
    }

    /**
     * 处理拍照
     */
    private void handleTakePhoto() {
        if (mMaxChooseCount == 1) {
            // 单选
            takePhoto();
        } else if (mPicAdapter.getSelectedCount() == mMaxChooseCount) {
            toastMaxCountTip();
        } else {
            takePhoto();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        try {
            startActivityForResult(mPhotoHelper.getTakePhotoIntent(), REQUEST_TAKE_PHOTO);
        } catch (Exception e) {
            BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
        }
    }

    /**
     * 跳转到图片选择预览界面
     *
     * @param position 当前点击的item的索引位置
     */
    private void changeToPreview(int position) {
        int currentPosition = position;
        if (mCurrentPhotoFolderModel.isTakePhotoEnabled()) {
            currentPosition--;
        }

        Intent intent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .submit(mPreviewSubmit)
                .actionBarColor(mActionBarColor)
                .actionBarTextColor(mActionBarTextColor)
                .backResId(mBackResId)
                .backgroundColor(mBackgroundColor)
                .isHidden(mPreviewIsHidden)
                .previewPhotos(mPicAdapter.getData())
                .selectedPhotos(mPicAdapter.getSelectedPhotos())
                .maxChooseCount(mMaxChooseCount)
                .currentPosition(currentPosition)
                .isFromTakePhoto(false)
                .build();

        startActivityForResult(intent, REQUEST_PREVIEW);
    }

    /**
     * 处理点击选择按钮事件
     *
     * @param position 当前点击的item的索引位置
     */
    private void handleClickSelectFlagIv(int position) {
        String currentPhoto = mPicAdapter.getItem(position);

        if (mMaxChooseCount == 1) {// 单选
            if (mPicAdapter.getSelectedCount() > 0) {
                String selectedPhoto = mPicAdapter.getSelectedPhotos().remove(0);

                if (TextUtils.equals(selectedPhoto, currentPhoto)) {
                    mPicAdapter.notifyItemChanged(position);

                } else {
                    int preSelectedPhotoPosition = mPicAdapter.getData().indexOf(selectedPhoto);
                    mPicAdapter.notifyItemChanged(preSelectedPhotoPosition);
                    mPicAdapter.getSelectedPhotos().add(currentPhoto);
                    mPicAdapter.notifyItemChanged(position);
                }

            } else {
                mPicAdapter.getSelectedPhotos().add(currentPhoto);
                mPicAdapter.notifyItemChanged(position);
            }

            renderTopRightBtn();

        } else {// 多选
            if (!mPicAdapter.getSelectedPhotos().contains(currentPhoto) && mPicAdapter.getSelectedCount() == mMaxChooseCount) {
                toastMaxCountTip();

            } else {
                if (mPicAdapter.getSelectedPhotos().contains(currentPhoto)) {
                    mPicAdapter.getSelectedPhotos().remove(currentPhoto);

                } else {
                    mPicAdapter.getSelectedPhotos().add(currentPhoto);
                }

                mPicAdapter.notifyItemChanged(position);

                renderTopRightBtn();
            }
        }
    }

    private void reloadPhotos(int position) {
        if (position < mPhotoFolderModels.size()) {
            mCurrentPhotoFolderModel = mPhotoFolderModels.get(position);
            if (tv_title != null) {
                tv_title.setText(mCurrentPhotoFolderModel.name);
            }

            mPicAdapter.setPhotoFolderModel(mCurrentPhotoFolderModel);
        }
    }

    @Override
    public void onPostExecute(List<BGAPhotoFolderModel> photoFolderModels) {
        dismissLoadingDialog();
        mLoadPhotoTask = null;
        mPhotoFolderModels = photoFolderModels;
        reloadPhotos(mPhotoFolderPw == null ? 0 : mPhotoFolderPw.getCurrentPosition());
    }

    @Override
    public void onTaskCancelled() {
        dismissLoadingDialog();
        mLoadPhotoTask = null;
    }

    private void cancelLoadPhotoTask() {
        if (mLoadPhotoTask != null) {
            mLoadPhotoTask.cancelTask();
            mLoadPhotoTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        cancelLoadPhotoTask();
        super.onDestroy();
    }

}