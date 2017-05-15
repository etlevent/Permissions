package cherry.permission.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2017/5/15.
 */

public class Permissions {

    public static boolean hasSelfPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean shouldShowRequestPermissionRational(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return true;
        }
        return false;
    }

    public static boolean shouldShowRequestPermissionRational(Fragment fragment, String... permissions) {
        for (String permission : permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission))
                return true;
        }
        return false;
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {
        Fragment parentFragment = null;
        while (fragment.getParentFragment() != null) {
            parentFragment = fragment.getParentFragment();
        }
        if (parentFragment == null) {
            fragment.requestPermissions(permissions, requestCode);
        } else {
            parentFragment.requestPermissions(permissions, requestCode);
        }
    }
}
