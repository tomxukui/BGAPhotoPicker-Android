package cn.bingoogolapple.photopicker.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.bingoogolapple.photopicker.demo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_circle).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MomentAddActivity.class)));

        findViewById(R.id.btn_system).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SystemGalleryActivity.class)));
    }

}