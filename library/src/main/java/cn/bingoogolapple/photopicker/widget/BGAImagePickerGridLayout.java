package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;

public class BGAImagePickerGridLayout extends ViewGroup {

    private int mSpace;
    private int mItemSize;
    private int mRow;
    private int mCornerRadius;

    private int mOldNum;
    private List<String> mPhotos;
    private LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;

    public BGAImagePickerGridLayout(Context context) {
        super(context);
        initData(context, null, 0);
    }

    public BGAImagePickerGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs, 0);
    }

    public BGAImagePickerGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BGAImagePickerGridLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(context, attrs, defStyleAttr);
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        mInflater = LayoutInflater.from(context);
        mSpace = BGABaseAdapterUtil.dp2px(10);
        mCornerRadius = 0;
        mPhotos = new ArrayList<>();
        mOldNum = 0;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BGAImagePickerGridLayout, defStyleAttr, 0);

            mSpace = ta.getDimensionPixelSize(R.styleable.BGAImagePickerGridLayout_bga_ipgl_space, mSpace);
            mCornerRadius = ta.getDimensionPixelSize(R.styleable.BGAImagePickerGridLayout_bga_ipgl_cornerRadius, mCornerRadius);

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
                final View layout = getChildAt(i);
                final BGAImageView iv_item_photo = layout.findViewById(R.id.iv_item_photo);
                final ImageView iv_item_flag = layout.findViewById(R.id.iv_item_flag);

                final int position = i;
                final String photo = mPhotos.get(i);

                BGAImage.display(iv_item_photo, 0, photo, mItemSize);
                iv_item_photo.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onPhotoClick(iv_item_photo, position, photo, mPhotos);
                        }
                    }

                });
                iv_item_flag.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onPhotoDelete(iv_item_flag, position, photo, mPhotos);
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

                layout.layout(left, top, left + mItemSize, top + mItemSize);
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

    public void setCornerRadius(int cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public void setSpace(int space) {
        mSpace = space;
    }

    private View createItemView() {
        View layout = mInflater.inflate(R.layout.bga_pp_item_gridlayout_image_picker, this, false);
        return layout;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onPhotoClick(BGAImageView view, int position, String photo, List<String> photos);

        void onPhotoDelete(View view, int position, String photo, List<String> photos);
    }

}
