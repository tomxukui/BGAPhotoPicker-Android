package cn.bingoogolapple.photopicker.demo.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class AndroidLifecycleUtil {

    public static boolean canLoadImage(Fragment fragment) {
        if (fragment == null) {
            return false;
        }

        FragmentActivity activity = fragment.getActivity();

        return canLoadImage(activity);
    }

    public static boolean canLoadImage(Context context) {
        if (context == null) {
            return false;
        }

        if (!(context instanceof Activity)) {
            return false;
        }

        Activity activity = (Activity) context;

        return canLoadImage(activity);
    }

    public static boolean canLoadImage(Activity activity) {
        if (activity == null) {
            return false;
        }

        boolean destroyed = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed();
        if (destroyed || activity.isFinishing()) {
            return false;
        }

        return true;
    }

    public static boolean canLoadImage(View view) {
        if (view == null) {
            return false;
        }

        Context context = view.getContext();

        return canLoadImage(context);
    }

}
