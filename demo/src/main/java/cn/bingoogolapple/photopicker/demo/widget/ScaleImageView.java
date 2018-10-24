package cn.bingoogolapple.photopicker.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import cn.bingoogolapple.photopicker.demo.R;

/**
 * Created by xukui on 2018-10-24.
 */
public class ScaleImageView extends AppCompatImageView {

    private float mScale;

    public ScaleImageView(Context context) {
        super(context);
        initData(context, null, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        mScale = 1.0f;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView, defStyleAttr, 0);

            mScale = ta.getFloat(R.styleable.ScaleImageView_siv_scale, mScale);

            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width / mScale);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

}
