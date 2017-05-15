package permission.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/15.
 */

public class Permissions {

    private static final String TAG = "Permissions";
    private static Map<Class<?>, Constructor<? extends Action>> PERMISSIONS_CONSTRUCTOR = new HashMap<>();
    private static Map<Class<?>, Action> PERMISSIONS = new HashMap<>();

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
        if (!hasSelfPermissions(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            Action action = createAction(activity);
            action.permissionGranted(requestCode);
        }
    }

    public static void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {
        if (hasSelfPermissions(fragment.getContext(), permissions)) {
            Action action = createAction(fragment);
            action.permissionGranted(requestCode);
            return;
        }
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

    public static void onRequestPermissionsResult(Object object, int requestCode,
                                                  @NonNull String[] permissions,
                                                  @NonNull int[] grantResults) {
        Action action = createAction(object);
        if (action.checkPermissions(permissions)) {
            action.permissionGranted(requestCode);
        } else {
            action.permissionDenied(requestCode);
        }
    }


    private static Action createAction(Object target) {
        Class<?> targetClass = target.getClass();
        Action action = PERMISSIONS.get(targetClass);
        if (action != null)
            return action;
        Constructor<? extends Action> constructor = findPermissionConstructor(targetClass);
        if (constructor == null) {
            Log.e(TAG, "No Constructor Find for " + targetClass.getName());
            return null;
        }
        try {
            action = constructor.newInstance(target);
            PERMISSIONS.put(targetClass, action);
            return action;
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    private static Constructor<? extends Action> findPermissionConstructor(Class<?> targetClass) {
        Constructor<? extends Action> constructor = PERMISSIONS_CONSTRUCTOR.get(targetClass);
        if (constructor != null) {
            return constructor;
        }
        String className = targetClass.getName();
        try {
            Class<?> permissionClass = Class.forName(className + "_Permissions");
            constructor = (Constructor<? extends Action>) permissionClass.getConstructor(targetClass);
        } catch (ClassNotFoundException e) {
            constructor = findPermissionConstructor(targetClass.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("cannot find constructor for " + className, e);
        }
        return constructor;
    }
}