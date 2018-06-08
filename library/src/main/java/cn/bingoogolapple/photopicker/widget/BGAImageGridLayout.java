package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;

public class BGAImageGridLayout extends ViewGroup {

    private int mSpace;
    private int mItemSize;
    private int mRow;
    private int mPlaceholder;
    private int mCornerRadius;
    private int mOldNum;
    private List<String> mPhotos;

    private OnItemClickListener mOnItemClickListener;

    public BGAImageGridLayout(Context context) {
        super(context);
        initData(context, null, 0);
    }

    public BGAImageGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs, 0);
    }

    public BGAImageGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BGAImageGridLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(context, attrs, defStyleAttr);
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        mSpace = BGABaseAdapterUtil.dp2px(10);
        mPlaceholder = 0;
        mCornerRadius = 0;
        mPhotos = new ArrayList<>();
        mOldNum = 0;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BGAImageGridLayout, defStyleAttr, 0);

            mSpace = ta.getDimensionPixelSize(R.styleable.BGAImageGridLayout_bga_igl_space, mSpace);
            mPlaceholder = ta.getResourceId(R.styleable.BGAImageGridLayout_bga_igl_placeholder, mPlaceholder);
            mCornerRadius = ta.getDimensionPixelSize(R.styleable.BGAImageGridLayout_bga_igl_cornerRadius, mCornerRadius);

            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        if (count > 0) {
            this.setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

            mRow = count / 3 + (count % 3 == 0 ? 0 : 1);
            mItemSize = (getMeasuredWidth() - mSpace * 2) / 3;

            widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mItemSize * mRow + mSpace * (mRow - 1), MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        if (count > 0) {
            int left = 0;
            int top = 0;
            int column = (count == 4 ? 2 : Math.min(count, 3));

            for (int i = 0; i < count; i++) {
                final BGAImageView iv = (BGAImageView) getChildAt(i);
                final int position = i;
                final String photo = mPhotos.get(i);

                BGAImage.display(iv, mPlaceholder, photo, mItemSize);
                iv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(iv, position, photo, mPhotos);
                        }
                    }

                });

                if (i % column > 0) {
                    left += mSpace;

                } else {
                    left = 0;

                    if ((i / column) % mRow > 0) {
                        top += mSpace + mItemSize;
                    }
                }

                iv.layout(left, top, left + mItemSize, top + mItemSize);
                left += mItemSize;
            }
        }
    }

    public void setData(List<String> photos) {
        mPhotos.clear();
        if (photos != null) {
            mPhotos.addAll(photos);
        }

        if (mPhotos.size() == 0) {
            removeAllViews();
            mOldNum = 0;

        } else {
            if (mOldNum == 0) {
                for (int i = 0; i < mPhotos.size(); i++) {
                    addView(createItemView());
                }

            } else {
                if (mOldNum > mPhotos.size()) {//新创建的比之前的要少，则减去多余的部分
                    removeViews(mPhotos.size() - 1, mOldNum - mPhotos.size());

                } else if (mOldNum < mPhotos.size()) {//新创建的比之前的要少，则添加缺少的部分
                    for (int i = 0; i < mPhotos.size() - mOldNum; i++) {
                        addView(createItemView());
                    }
                }
            }

            mOldNum = mPhotos.size();
        }
    }

    public void setPlaceholder(int placeholder) {
        mPlaceholder = placeholder;
    }

    public void setCornerRadius(int cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public void setSpace(int space) {
        mSpace = space;
    }

    private BGAImageView createItemView() {
        BGAImageView iv = new BGAImageView(getContext());
        iv.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setCornerRadius(mCornerRadius);
        return iv;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(BGAImageView view, int position, String photo, List<String> photos);
    }

}
