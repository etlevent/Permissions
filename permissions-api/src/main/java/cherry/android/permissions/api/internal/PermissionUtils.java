package cherry.android.permissions.api.internal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

/**
 * Created by Administrator on 2017/5/15.
 */

public class PermissionUtils {

    private static final String FRAGMENT_TAG = "leon.android.permissions";

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
        Activity activity = getActivity(target);
        return shouldShowRequestPermissionRationalInternal(activity, permissions);
    }

    public static void requestPermissions(Object target, String[] permissions, int requestCode) {
        Activity activity = getActivity(target);
        Log.d("Permission", "activity=" + activity);
        if (activity instanceof FragmentActivity) {
            PermissionFragmentV4 fragmentV4 = getPermissionFragmentV4((FragmentActivity) activity);
            fragmentV4.setTarget(target);
            fragmentV4.requestPermissions(permissions, requestCode);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionFragment fragment = getPermissionFragment(activity);
                fragment.setTarget(target);
                fragment.requestPermissions(permissions, requestCode);
            } else {
                throw new IllegalArgumentException(android.app.Fragment.class + ".requestPermissions(...) require api 23.");
            }
        }
    }

    private static boolean shouldShowRequestPermissionRationalInternal(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return true;
        }
        return false;
    }

    private static PermissionFragment getPermissionFragment(@NonNull Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        PermissionFragment fragment = (PermissionFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new PermissionFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    private static PermissionFragmentV4 getPermissionFragmentV4(@NonNull FragmentActivity activity) {
        android.support.v4.app.FragmentManager supportFM = activity.getSupportFragmentManager();
        PermissionFragmentV4 fragmentV4 = (PermissionFragmentV4) supportFM.findFragmentByTag(FRAGMENT_TAG);
        if (fragmentV4 == null) {
            fragmentV4 = new PermissionFragmentV4();
            supportFM.beginTransaction()
                    .add(fragmentV4, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
            supportFM.executePendingTransactions();
        }
        return fragmentV4;
    }

    public static Context getContext(@NonNull Object target) {
        if (target instanceof Activity) {
            Activity activity = (Activity) target;
            return activity;
        } else if (target instanceof Fragment) {
            Fragment fragment = (Fragment) target;
            return fragment.getActivity();
        } else if (target instanceof android.support.v4.app.Fragment) {
            android.support.v4.app.Fragment fragmentV4 = (android.support.v4.app.Fragment) target;
            return fragmentV4.getActivity();
        } else if (target instanceof Context) {
            return (Context) target;
        } else {
            throw new IllegalArgumentException("cannot get Context from target: " + target);
        }
    }

    public static Activity getActivity(@NonNull Object target) {
        if (target instanceof Activity) {
            Activity activity = (Activity) target;
            return activity;
        } else if (target instanceof Fragment) {
            Fragment fragment = (Fragment) target;
            return fragment.getActivity();
        } else if (target instanceof android.support.v4.app.Fragment) {
            android.support.v4.app.Fragment fragmentV4 = (android.support.v4.app.Fragment) target;
            return fragmentV4.getActivity();
        } else {
            throw new IllegalArgumentException("target must be Activity or Fragment :" + target);
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