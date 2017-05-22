package cherry.android.permissions.api.internal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

/**
 * Created by Administrator on 2017/5/15.
 */

public class PermissionUtils {

    private static final String TAG = "PermissionUtils";

    public static boolean hasSelfPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
                if (!checkSelfPermissionForXiaomi(context, permission)) {
                    return false;
                }
            } else {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkSelfPermissionForXiaomi(Context context, String permission) {
        String permissionToOp = AppOpsManagerCompat.permissionToOp(permission);
        if (permissionToOp == null)
            return true;
        int noteOp = AppOpsManagerCompat.noteOp(context, permissionToOp, Process.myUid(), context.getPackageName());
        return noteOp == AppOpsManagerCompat.MODE_ALLOWED
                && PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean shouldShowRequestPermissionRational(Object target, String... permissions) {
        if (target instanceof Activity) {
            Activity activity = (Activity) target;
            return shouldShowRequestPermissionRational(activity, permissions);
        } else if (target instanceof Fragment) {
            Fragment fragment = (Fragment) target;
            return shouldShowRequestPermissionRational(fragment, permissions);
        } else {
            throw new IllegalArgumentException("target must be Activity or Fragment :" + target);
        }
    }

    private static boolean shouldShowRequestPermissionRational(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return true;
        }
        return false;
    }

    private static boolean shouldShowRequestPermissionRational(Fragment fragment, String... permissions) {
        for (String permission : permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission))
                return true;
        }
        return false;
    }

    public static void requestPermissions(Object target, String[] permissions, int requestCode) {
        if (target instanceof Activity) {
            Activity activity = (Activity) target;
            requestPermissions(activity, permissions, requestCode);
        } else if (target instanceof Fragment) {
            Fragment fragment = (Fragment) target;
            requestPermissions(fragment, permissions, requestCode);
        } else {
            throw new IllegalArgumentException("target must be Activity or Fragment :" + target);
        }
    }

    private static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    private static void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {
        fragment.requestPermissions(permissions, requestCode);
    }

    public static Context getContext(Object target) {
        if (target instanceof Activity) {
            Activity activity = (Activity) target;
            return activity.getBaseContext();
        } else if (target instanceof Fragment) {
            Fragment fragment = (Fragment) target;
            return fragment.getContext();
        } else if (target instanceof Context) {
            return (Context) target;
        } else {
            throw new IllegalArgumentException("cannot get Context from target: " + target);
        }
    }

    public static <T> T castTarget(Object target, Class<T> cls) {
        try {
            return cls.cast(target);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Target '"
                    + target
                    + " was of the wrong type. See cause for more info.", e);
        }
    }
}