package cn.bingoogolapple.photopicker.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

public class BGAViewPageAdapter extends PagerAdapter {

    private List<String> mPhotos;
    private OnItemClickListener mOnItemClickListener;

    public BGAViewPageAdapter(List<String> photos) {
        mPhotos = (photos == null ? new ArrayList<String>() : photos);
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        final String photo = mPhotos.get(position);

        final SubsamplingScaleImageView iv = new SubsamplingScaleImageView(container.getContext());
        container.addView(iv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setImage(ImageSource.uri(photo));
        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(iv, position, photo);
                }
            }

        });
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position, String photo);

    }

}