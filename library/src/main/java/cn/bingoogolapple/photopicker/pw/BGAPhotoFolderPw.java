package cn.bingoogolapple.photopicker.pw;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;


/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午6:19
 * 描述:选择图片目录的PopupWindow
 */
public class BGAPhotoFolderPw extends BGABasePopupWindow implements BGAOnRVItemClickListener {

    public static final int ANIM_DURATION = 300;

    private LinearLayout mRootLl;
    private RecyclerView mContentRv;

    private FolderAdapter mFolderAdapter;
    private Delegate mDelegate;

    private int mCurrentPosition;

    public BGAPhotoFolderPw(Activity activity, View anchorView, Delegate delegate) {
        super(activity, R.layout.bga_pw_photo_folder, anchorView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mDelegate = delegate;
    }

    @Override
    protected void initView() {
        mRootLl = findViewById(R.id.ll_photo_folder_root);
        mContentRv = findViewById(R.id.rv_photo_folder_content);
    }

    @Override
    protected void setListener() {
        mRootLl.setOnClickListener(this);
        mFolderAdapter = new FolderAdapter(mContentRv);
        mFolderAdapter.setOnRVItemClickListener(this);
    }

    @Override
    protected void processLogic() {
        setAnimationStyle(android.R.style.Animation);
        setBackgroundDrawable(new ColorDrawable(0x90000000));

        mContentRv.setLayoutManager(new LinearLayoutManager(mActivity));
        mContentRv.setAdapter(mFolderAdapter);
    }

    /**
     * 设置目录数据集合
     *
     * @param data
     */
    public void setData(List<BGAPhotoFolderModel> data) {
        mFolderAdapter.setData(data);
    }

    @Override
    public void show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int[] location = new int[2];
            mAnchorView.getLocationInWindow(location);
            int offsetY = location[1] + mAnchorView.getHeight();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                setHeight(BGAPhotoPickerUtil.getScreenHeight() - offsetY);
            }
            showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 0, offsetY);

        } else {
            showAsDropDown(mAnchorView);
        }

        ViewCompat.animate(mContentRv).translationY(-mWindowRootView.getHeight()).setDuration(0).start();
        ViewCompat.animate(mContentRv).translationY(0).setDuration(ANIM_DURATION).start();
        ViewCompat.animate(mRootLl).alpha(0).setDuration(0).start();
        ViewCompat.animate(mRootLl).alpha(1).setDuration(ANIM_DURATION).start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_photo_folder_root) {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        ViewCompat.animate(mContentRv).translationY(-mWindowRootView.getHeight()).setDuration(ANIM_DURATION).start();
        ViewCompat.animate(mRootLl).alpha(1).setDuration(0).start();
        ViewCompat.animate(mRootLl).alpha(0).setDuration(ANIM_DURATION).start();

        if (mDelegate != null) {
            mDelegate.executeDismissAnim();
        }

        mContentRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                BGAPhotoFolderPw.super.dismiss();
            }
        }, ANIM_DURATION);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        if (mDelegate != null && mCurrentPosition != position) {
            mDelegate.onSelectedFolder(position);
        }

        mCurrentPosition = position;

        dismiss();
    }

    private class FolderAdapter extends BGARecyclerViewAdapter<BGAPhotoFolderModel> {

        private int mImageWidth;
        private int mImageHeight;

        public FolderAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.bga_item_photo_folder);
            mData = new ArrayList<>();
            mImageWidth = BGABaseAdapterUtil.dp2px(80);
            mImageHeight = BGABaseAdapterUtil.dp2px(60);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, BGAPhotoFolderModel model) {
            helper.setText(R.id.tv_item_photo_folder_name, model.name);
            helper.setText(R.id.tv_item_photo_folder_count, String.valueOf(model.getCount()) + "张");
            BGAImage.display(helper.getImageView(R.id.iv_item_photo_folder_photo), R.mipmap.bga_ic_holder_dark, model.coverPath, mImageWidth, mImageHeight);
        }

    }

    public interface Delegate {
        void onSelectedFolder(int position);

        void executeDismissAnim();
    }

}