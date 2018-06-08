package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

public class BGANinePhotoLayout extends FrameLayout {

    private BGAImageView mPhotoIv;
    private BGAImageGridLayout mPhotoGrid;

    private Delegate mDelegate;

    private int mItemCornerRadius;
    private boolean mShowAsLargeWhenOnlyOne;
    private float mRadioWhenOnlyOne;
    private int mItemWhiteSpacing;
    private int mPlaceholderDrawableResId;

    private List<String> mPhotos;
    private int mCurrentClickItemPosition;

    public BGANinePhotoLayout(@NonNull Context context) {
        super(context);
        initData(context, null, 0);
        initView();
    }

    public BGANinePhotoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs, 0);
        initView();
    }

    public BGANinePhotoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BGANinePhotoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(context, attrs, defStyleAttr);
        initView();
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        mShowAsLargeWhenOnlyOne = true;
        mItemCornerRadius = 0;
        mItemWhiteSpacing = BGABaseAdapterUtil.dp2px(4);
        mPlaceholderDrawableResId = R.mipmap.bga_pp_ic_holder_light;
        mRadioWhenOnlyOne = 0.6f;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BGANinePhotoLayout, defStyleAttr, 0);

            mShowAsLargeWhenOnlyOne = ta.getBoolean(R.styleable.BGANinePhotoLayout_bga_npl_showAsLargeWhenOnlyOne, mShowAsLargeWhenOnlyOne);
            mItemCornerRadius = ta.getDimensionPixelSize(R.styleable.BGANinePhotoLayout_bga_npl_itemCornerRadius, mItemCornerRadius);
            mItemWhiteSpacing = ta.getDimensionPixelSize(R.styleable.BGANinePhotoLayout_bga_npl_itemWhiteSpacing, mItemWhiteSpacing);
            mPlaceholderDrawableResId = ta.getResourceId(R.styleable.BGANinePhotoLayout_bga_npl_placeholderDrawable, mPlaceholderDrawableResId);
            mRadioWhenOnlyOne = ta.getFloat(R.styleable.BGANinePhotoLayout_bga_npl_radioWhenOnlyOne, mRadioWhenOnlyOne);

            ta.recycle();
        }
    }

    private void initView() {
        mPhotoIv = new BGAImageView(getContext());
        mPhotoIv.setClickable(true);
        mPhotoIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentClickItemPosition = 0;

                if (mDelegate != null) {
                    mDelegate.onClickNinePhotoItem(BGANinePhotoLayout.this, v, mCurrentClickItemPosition, mPhotos.get(mCurrentClickItemPosition), mPhotos);
                }
            }

        });

        mPhotoGrid = new BGAImageGridLayout(getContext());
        mPhotoGrid.setCornerRadius(mItemCornerRadius);
        mPhotoGrid.setPlaceholder(mPlaceholderDrawableResId);
        mPhotoGrid.setSpace(mItemWhiteSpacing);
        mPhotoGrid.setOnItemClickListener(new BGAImageGridLayout.OnItemClickListener() {

            @Override
            public void onItemClick(BGAImageView view, int position, String photo, List<String> photos) {
                mCurrentClickItemPosition = position;

                if (mDelegate != null) {
                    mDelegate.onClickNinePhotoItem(BGANinePhotoLayout.this, view, mCurrentClickItemPosition, mPhotos.get(mCurrentClickItemPosition), mPhotos);
                }
            }

        });

        addView(mPhotoIv, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mPhotoGrid, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 设置图片路径数据集合
     *
     * @param photos
     */
    public void setData(List<String> photos) {
        mPhotos = (photos == null ? new ArrayList<String>() : photos);

        if (mPhotos.size() == 0) {
            setVisibility(GONE);

        } else {
            setVisibility(VISIBLE);

            if (photos.size() == 1 && mShowAsLargeWhenOnlyOne) {
                mPhotoGrid.setVisibility(GONE);
                mPhotoIv.setVisibility(VISIBLE);

                int size = (int) (BGAPhotoPickerUtil.getScreenWidth() * mRadioWhenOnlyOne);
                mPhotoIv.setMaxWidth(size);
                mPhotoIv.setMaxHeight(size);

                if (mItemCornerRadius > 0) {
                    mPhotoIv.setCornerRadius(mItemCornerRadius);
                }

                BGAImage.display(mPhotoIv, mPlaceholderDrawableResId, photos.get(0), size);

            } else {
                mPhotoIv.setVisibility(GONE);
                mPhotoGrid.setVisibility(VISIBLE);
                mPhotoGrid.setData(photos);
            }
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public List<String> getData() {
        return mPhotos == null ? new ArrayList<String>() : mPhotos;
    }

    public int getCount() {
        return getData().size();
    }

    public String getCurrentClickItem() {
        return getData().get(mCurrentClickItemPosition);
    }

    public int getCurrentClickItemPosition() {
        return mCurrentClickItemPosition;
    }

    public interface Delegate {
        void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);
    }

}