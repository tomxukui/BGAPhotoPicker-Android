package cn.bingoogolapple.photopicker.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.demo.R;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 你自己项目里「可以不继承 BGAPPToolbarActivity」，我在这里继承 BGAPPToolbarActivity 只是为了方便写 Demo
 */
public class MomentAddActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PRC_PHOTO_PICKER = 1;
    private static final int RC_CHOOSE_PHOTO = 1;
    private static final int RC_PHOTO_PREVIEW = 2;

    private BGASortableNinePhotoLayout photoLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_add);
        initView();
    }

    private void initView() {
        photoLayout = findViewById(R.id.photoLayout);
        photoLayout.setDelegate(new BGASortableNinePhotoLayout.Delegate() {

            @Override
            public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
                choicePhotoWrapper();
            }

            @Override
            public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                photoLayout.removeItem(position);
            }

            @Override
            public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                Intent intent = new BGAPhotoPreviewActivity.IntentBuilder(MomentAddActivity.this)
                        .selectedPhotos(models)
                        .currentPosition(position)
                        .build();

                startActivityForResult(intent, RC_PHOTO_PREVIEW);
            }

            @Override
            public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {
                Toast.makeText(MomentAddActivity.this, "排序发生变化", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(photoLayout.getMaxItemCount()) // 图片选择张数的最大值
                    .selectedPhotos(photoLayout.getData()) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .spanCount(4)
                    .build();

            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);

        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            photoLayout.setData(BGAPhotoPickerActivity.getSelectedPhotos(data));

        } else if (requestCode == RC_PHOTO_PREVIEW) {
            photoLayout.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
    }

}