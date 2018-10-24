package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/25 下午5:03
 * 描述:
 */
public class BGAImage {

    private static final String TAG = BGAImage.class.getSimpleName();
    private static BGAImageLoader sImageLoader;

    private BGAImage() {
    }

    private static final BGAImageLoader getImageLoader() {
        if (sImageLoader == null) {
            synchronized (BGAImage.class) {
                sImageLoader = new BGAGlideImageLoader();
            }
        }
        return sImageLoader;
    }

    public static void display(ImageView imageView, @DrawableRes int loadingResId, @DrawableRes int failResId, String path, int width, int height, final BGAImageLoader.DisplayDelegate delegate) {
        try {
            getImageLoader().display(imageView, path, loadingResId, failResId, width, height, delegate);
        } catch (Exception e) {
            Log.d(TAG, "显示图片失败：" + e.getMessage());
        }
    }

    public static void display(ImageView imageView, @DrawableRes int placeholderResId, String path, int width, int height, final BGAImageLoader.DisplayDelegate delegate) {
        display(imageView, placeholderResId, placeholderResId, path, width, height, delegate);
    }

    public static void display(ImageView imageView, @DrawableRes int placeholderResId, String path, int width, int height) {
        display(imageView, placeholderResId, path, width, height, null);
    }

    public static void display(ImageView imageView, @DrawableRes int placeholderResId, String path, int size) {
        display(imageView, placeholderResId, path, size, size);
    }

    public static void download(String path, final BGAImageLoader.DownloadDelegate delegate) {
        try {
            getImageLoader().download(path, delegate);
        } catch (Exception e) {
            Log.d(TAG, "下载图片失败：" + e.getMessage());
        }
    }

    /**
     * 暂停加载
     *
     * @param activity
     */
    public static void pause(Activity activity) {
        getImageLoader().pause(activity);
    }

    /**
     * @param activity
     */
    public static void resume(Activity activity) {
        getImageLoader().resume(activity);
    }

    public static void clear(Activity activity, View view) {
        getImageLoader().clear(activity, view);
    }

}
