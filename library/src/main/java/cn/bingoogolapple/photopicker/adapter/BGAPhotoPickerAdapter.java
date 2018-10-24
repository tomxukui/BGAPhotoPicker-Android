package cn.bingoogolapple.photopicker.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

public class BGAPhotoPickerAdapter extends BGARecyclerViewAdapter<String> {

    private List<String> mSelectedPhotos = new ArrayList<>();
    private int mPhotoSize;
    private boolean mTakePhotoEnabled;

    public BGAPhotoPickerAdapter(RecyclerView recyclerView) {
        super(recyclerView);
        mPhotoSize = BGAPhotoPickerUtil.getScreenWidth() / 6;
    }

    @Override
    public int getItemViewType(int position) {
        if (mTakePhotoEnabled && position == 0) {
            return R.layout.bga_item_photo_camera;

        } else {
            return R.layout.bga_item_photo_picker;
        }
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
        if (viewType == R.layout.bga_item_photo_camera) {
            helper.setItemChildClickListener(R.id.iv_camera);

        } else {
            helper.setItemChildClickListener(R.id.iv_photo);
            helper.setItemChildClickListener(R.id.iv_check);
        }
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, String model) {
        if (getItemViewType(position) == R.layout.bga_item_photo_picker) {
            BGAImage.display(helper.getImageView(R.id.iv_photo), R.mipmap.bga_ic_holder_dark, model, mPhotoSize);

            if (mSelectedPhotos.contains(model)) {
                helper.setImageResource(R.id.iv_check, R.mipmap.bga_ic_cb_checked);
                helper.getImageView(R.id.iv_photo).setColorFilter(Color.parseColor("#55000000"));

            } else {
                helper.setImageResource(R.id.iv_check, R.mipmap.bga_ic_cb_normal);
                helper.getImageView(R.id.iv_photo).setColorFilter(null);
            }
        }
    }

    public void setSelectedPhotos(List<String> selectedPhotos) {
        if (selectedPhotos != null) {
            mSelectedPhotos = selectedPhotos;
        }

        notifyDataSetChanged();
    }

    public List<String> getSelectedPhotos() {
        return mSelectedPhotos;
    }

    public int getSelectedCount() {
        return mSelectedPhotos.size();
    }

    public void setPhotoFolderModel(BGAPhotoFolderModel photoFolderModel) {
        mTakePhotoEnabled = photoFolderModel.isTakePhotoEnabled();
        setData(photoFolderModel.getPhotos());
    }

}
