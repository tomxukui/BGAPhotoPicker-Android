package cn.bingoogolapple.photopicker.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ablingbling.library.photoview.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGABrowserPhotoViewAttacher;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAImageView;

public class BGAPhotoPageAdapter extends PagerAdapter {

    private List<String> mPhotos;
    private PhotoViewAttacher.OnViewTapListener mOnViewTapListener;

    public BGAPhotoPageAdapter(PhotoViewAttacher.OnViewTapListener listener) {
        this(listener, null);
    }

    public BGAPhotoPageAdapter(PhotoViewAttacher.OnViewTapListener listener, List<String> photos) {
        mOnViewTapListener = listener;
        mPhotos = (photos == null ? new ArrayList<String>() : photos);
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final BGAImageView imageView = new BGAImageView(container.getContext());
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final BGABrowserPhotoViewAttacher photoViewAttacher = new BGABrowserPhotoViewAttacher(imageView);
        photoViewAttacher.setOnViewTapListener(mOnViewTapListener);
        imageView.setDelegate(new BGAImageView.Delegate() {

            @Override
            public void onDrawableChanged(Drawable drawable) {
                if (drawable != null && drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth() && drawable.getIntrinsicHeight() > BGAPhotoPickerUtil.getScreenHeight()) {
                    photoViewAttacher.setIsSetTopCrop(true);
                    photoViewAttacher.setUpdateBaseMatrix();
                } else {
                    photoViewAttacher.update();
                }
            }

        });

        BGAImage.display(imageView, 0, mPhotos.get(position), BGAPhotoPickerUtil.getScreenWidth(), BGAPhotoPickerUtil.getScreenHeight());
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setNewData(List<String> photos) {
        mPhotos.clear();

        if (photos != null) {
            mPhotos.addAll(photos);
        }

        notifyDataSetChanged();
    }

    public List<String> getData() {
        return mPhotos;
    }

    public String getPosition(int position) {
        return mPhotos == null ? "" : mPhotos.get(position);
    }

    public void remove(int position) {
        if (position >= 0 && position < mPhotos.size()) {
            mPhotos.remove(position);
            notifyDataSetChanged();
        }
    }

}