package cn.bingoogolapple.photopicker.model;

import java.util.ArrayList;

public class BGAPhotoFolderModel {

    public String name;
    public String coverPath;
    private ArrayList<String> mPhotos = new ArrayList<>();
    private boolean mTakePhotoEnabled;

    public BGAPhotoFolderModel(boolean takePhotoEnabled) {
        mTakePhotoEnabled = takePhotoEnabled;

        if (takePhotoEnabled) {//拍照
            mPhotos.add("");
        }
    }

    public BGAPhotoFolderModel(String name, String coverPath) {
        this.name = name;
        this.coverPath = coverPath;
    }

    public boolean isTakePhotoEnabled() {
        return mTakePhotoEnabled;
    }

    public void addLastPhoto(String photoPath) {
        mPhotos.add(photoPath);
    }

    public ArrayList<String> getPhotos() {
        return mPhotos;
    }

    public int getCount() {
        return mTakePhotoEnabled ? mPhotos.size() - 1 : mPhotos.size();
    }

}