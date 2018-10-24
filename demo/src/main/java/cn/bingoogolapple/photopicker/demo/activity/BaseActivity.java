package cn.bingoogolapple.photopicker.demo.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.blankj.utilcode.utils.ToastUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import cn.bingoogolapple.photopicker.demo.util.RuntimeRationale;

/**
 * Created by xukui on 2018-10-24.
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * Request permissions.
     */
    protected void requestPermission(Action<List<String>> granted, String... permissions) {
        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(granted)
                .onDenied(permissions1 -> {
                    ToastUtils.showShortToast("授权失败");
                    if (AndPermission.hasAlwaysDeniedPermission(this, permissions1)) {
                        showSettingDialog(permissions1);
                    }
                })
                .start();
    }

    /**
     * Request permissions.
     */
    protected void requestPermission(Action<List<String>> granted, String[]... groups) {
        AndPermission.with(this)
                .runtime()
                .permission(groups)
                .rationale(new RuntimeRationale())
                .onGranted(granted)
                .onDenied(permissions1 -> {
                    ToastUtils.showShortToast("授权失败");
                    if (AndPermission.hasAlwaysDeniedPermission(this, permissions1)) {
                        showSettingDialog(permissions1);
                    }
                })
                .start();
    }

    /**
     * Display setting dialog.
     */
    public void showSettingDialog(final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(this, permissions);
        String message = String.format("我们需要以下权限，请在设置中为我们开启：\n\n%1$s", TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("设置", (dialog, which) -> setPermission())
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * Set permissions.
     */
    private void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback(() -> {
                })
                .start();
    }

}
