package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import cn.bingoogolapple.photopicker.R;

public class BGAImageView extends AppCompatImageView {

    private int mDefaultImageId;
    private int mCornerRadius;
    private boolean mCircle;
    private int mBorderWidth;
    private int mBorderColor;

    private Paint mBorderPaint;

    private Delegate mDelegate;

    public BGAImageView(Context context) {
        super(context);
        initData(context, null, 0);
        initView();
    }

    public BGAImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs, 0);
        initView();
    }

    public BGAImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
        initView();
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        mCircle = false;
        mDefaultImageId = 0;
        mCornerRadius = 0;
        mBorderWidth = 0;
        mBorderColor = Color.WHITE;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BGAImageView, defStyleAttr, 0);

            mCircle = ta.getBoolean(R.styleable.BGAImageView_bga_iv_circle, mCircle);
            mDefaultImageId = ta.getResourceId(R.styleable.BGAImageView_android_src, mDefaultImageId);
            mCornerRadius = ta.getDimensionPixelSize(R.styleable.BGAImageView_bga_iv_cornerRadius, mCornerRadius);
            mBorderWidth = ta.getDimensionPixelSize(R.styleable.BGAImageView_bga_iv_borderWidth, mBorderWidth);
            mBorderColor = ta.getColor(R.styleable.BGAImageView_bga_iv_borderColor, mBorderColor);

            ta.recycle();
        }

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    private void initView() {
        if (mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        }
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        setImageDrawable(getResources().getDrawable(resId));
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof BitmapDrawable && mCornerRadius > 0) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            if (bitmap != null) {
                super.setImageDrawable(getRoundedDrawable(getContext(), bitmap, mCornerRadius));
            } else {
                super.setImageDrawable(drawable);
            }

        } else if (drawable instanceof BitmapDrawable && mCircle) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            if (bitmap != null) {
                super.setImageDrawable(getCircleDrawable(getContext(), bitmap));
            } else {
                super.setImageDrawable(drawable);
            }

        } else {
            super.setImageDrawable(drawable);
        }

        notifyDrawableChanged(drawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        heightMeasureSpec = widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCircle && mBorderWidth > 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 1.0f * mBorderWidth / 2, mBorderPaint);
        }
    }

    private void notifyDrawableChanged(Drawable drawable) {
        if (mDelegate != null) {
            mDelegate.onDrawableChanged(drawable);
        }
    }

    public void setCornerRadius(int cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public void setCircle(boolean circle) {
        mCircle = circle;
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public interface Delegate {
        void onDrawableChanged(Drawable drawable);
    }

    public static RoundedBitmapDrawable getCircleDrawable(Context context, Bitmap src) {
        Bitmap dst;
        if (src.getWidth() >= src.getHeight()) {
            dst = Bitmap.createBitmap(src, src.getWidth() / 2 - src.getHeight() / 2, 0, src.getHeight(), src.getHeight());
        } else {
            dst = Bitmap.createBitmap(src, 0, src.getHeight() / 2 - src.getWidth() / 2, src.getWidth(), src.getWidth());
        }

        RoundedBitmapDrawable circleDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), dst);
        circleDrawable.setAntiAlias(true);
        circleDrawable.setCircular(true);
        return circleDrawable;
    }

    public static RoundedBitmapDrawable getRoundedDrawable(Context context, Bitmap bitmap, float cornerRadius) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmapDrawable.setAntiAlias(true);
        roundedBitmapDrawable.setCornerRadius(cornerRadius);
        return roundedBitmapDrawable;
    }

}