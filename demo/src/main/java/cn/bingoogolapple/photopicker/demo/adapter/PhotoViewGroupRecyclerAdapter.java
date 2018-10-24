package cn.bingoogolapple.photopicker.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.demo.R;
import cn.bingoogolapple.photopicker.demo.util.AndroidLifecycleUtil;
import cn.bingoogolapple.photopicker.demo.util.GlideApp;
import cn.bingoogolapple.photopicker.util.DensityUtil;
import cn.bingoogolapple.photopicker.widget.ScaleImageView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by xukui on 2018-06-28.
 */
public class PhotoViewGroupRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PHOTO = 0;
    private static final int TYPE_ADD = 1;

    private int mMaxCount;
    private List<String> mPhotos;
    private OnItemClickListener mOnItemClickListener;
    private OnAddClickListener mOnAddClickListener;

    public PhotoViewGroupRecyclerAdapter(int maxCount) {
        mMaxCount = maxCount;
        mPhotos = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        int count = mPhotos.size();
        if (count < mMaxCount) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int count = mPhotos.size();
        if (count < mMaxCount) {
            return (position < count) ? TYPE_PHOTO : TYPE_ADD;

        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == TYPE_PHOTO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_photo_view_group, parent, false);
            return new ViewHolder0(view);

        } else if (viewType == TYPE_ADD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_photo_view_group, parent, false);
            return new ViewHolder1(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder0) {
            ViewHolder0 vh = (ViewHolder0) holder;
            String photo = mPhotos.get(position);
            vh.setData(photo);
            vh.iv_photo.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, photo, position);
                }
            });

        } else if (holder instanceof ViewHolder1) {
            ViewHolder1 vh = (ViewHolder1) holder;
            vh.iv_photo.setOnClickListener(v -> {
                if (mOnAddClickListener != null) {
                    mOnAddClickListener.onAddClick(v);
                }
            });
        }
    }

    public void setNewData(List<String> photos) {
        mPhotos.clear();

        if (mPhotos != null) {
            mPhotos.addAll(photos);
        }

        notifyDataSetChanged();
    }

    public void addData(List<String> photos) {
        mPhotos.addAll(photos);

        notifyDataSetChanged();
    }

    public List<String> getData() {
        return mPhotos;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnAddClickListener(OnAddClickListener listener) {
        mOnAddClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(View itemView, String photo, int position);

    }

    public interface OnAddClickListener {

        void onAddClick(View itemView);

    }

    static class ViewHolder0 extends RecyclerView.ViewHolder {

        ScaleImageView iv_photo;

        public ViewHolder0(View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
        }

        public void setData(String photo) {
            if (AndroidLifecycleUtil.canLoadImage(iv_photo)) {
                GlideApp.with(iv_photo)
                        .load(photo)
                        .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(new CenterCrop(), new RoundedCornersTransformation(DensityUtil.dp2px(3), 0))))
                        .placeholder(R.drawable.f_ic_photo_add)
                        .error(R.drawable.f_ic_photo_add)
                        .fallback(R.drawable.f_ic_photo_add)
                        .into(iv_photo);
            }
        }

    }

    static class ViewHolder1 extends RecyclerView.ViewHolder {

        ScaleImageView iv_photo;

        public ViewHolder1(View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);

            iv_photo.setImageResource(R.drawable.f_ic_photo_add);
        }

    }

}
