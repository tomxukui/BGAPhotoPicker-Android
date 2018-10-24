package cn.bingoogolapple.photopicker.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yanzhenjie.permission.Permission;

import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.demo.R;
import cn.bingoogolapple.photopicker.demo.adapter.PhotoViewGroupRecyclerAdapter;
import cn.bingoogolapple.photopicker.util.DensityUtil;
import cn.bingoogolapple.photopicker.widget.GridSpacingItemDecoration;

public class MomentAddActivity extends BaseActivity {

    private static final int SPAN_COUNT = 3;
    private static final int MAX_COUNT = 3;

    private static final int REQUEST_PICK_PHOTO = 1;

    private RecyclerView recyclerView;
    private PhotoViewGroupRecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_add);
        initData();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_PICK_PHOTO: {
                if (resultCode == RESULT_OK) {
                    List<String> photos = BGAPhotoPickerActivity.getSelectedPhotos(data);
                    mRecyclerAdapter.addData(photos);
                }
            }
            break;

            default:
                break;

        }
    }

    private void initData() {
        mRecyclerAdapter = new PhotoViewGroupRecyclerAdapter(MAX_COUNT);
//        mRecyclerAdapter.setOnItemClickListener((itemView, photo, position) -> requestPermission(data -> previewImgs(), Permission.Group.STORAGE));
        mRecyclerAdapter.setOnAddClickListener(itemView -> requestPermission(data -> pickImgs(), Permission.Group.STORAGE, Permission.Group.CAMERA));
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, DensityUtil.dp2px(23), false));
        recyclerView.setAdapter(mRecyclerAdapter);

    }

    private void pickImgs() {
        int count = MAX_COUNT - mRecyclerAdapter.getData().size();

        if (count <= 0) {
            return;
        }

        Intent intent = new BGAPhotoPickerActivity.IntentBuilder(this)
                .submit("完成")
                .maxChooseCount(count)// 图片选择张数的最大值
                .pauseOnScroll(false)// 滚动列表时是否暂停加载图片
                .build();

        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

}