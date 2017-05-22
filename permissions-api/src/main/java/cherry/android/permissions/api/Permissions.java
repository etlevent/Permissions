package cherry.android.permissions.api;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import cherry.android.permissions.api.internal.Action;

import static cherry.android.permissions.api.internal.PermissionUtils.shouldShowRequestPermissionRational;

/**
 * Created by Administrator on 2017/5/22.
 */

public final class Permissions {

    private static final String TAG = "Permissions";

    private static Map<Class<?>, Constructor<? extends Action>> PERMISSIONS_CONSTRUCTOR = new HashMap<>();
    private static Map<Class<?>, Action> PERMISSIONS = new HashMap<>();

    public static void permissionGranted(Object target, int requestCode) {
        Action action = createAction(target);
        if (action != null) {
            action.permissionGranted(requestCode);
        } else {
            Log.w(TAG, "target's proxy class " + target.getClass() + "_Permissions is not found");
        }
    }

    public static void permissionDenied(Object target, int requestCode, String[] permissions) {
        Action action = createAction(target);
        if (action != null) {
            if (!shouldShowRequestPermissionRational(target, permissions)
                    && action.shouldPermissionRationale(requestCode)) {
                action.showPermissionRationale(requestCode);
            } else {
                action.permissionDenied(requestCode);
            }
        } else {
            Log.w(TAG, "target's proxy class " + target.getClass() + "_Permissions is not found");
        }
    }

    private static Action createAction(Object target) {
        Class<?> targetClass = target.getClass();
        Action action = PERMISSIONS.get(targetClass);
        if (action != null) {
            action.updateTarget(target);
            return action;
        }
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
            throw new RuntimeException("Unable to create permissions instance.", cause);
        }
    }

    private static Constructor<? extends Action> findPermissionConstructor(Class<?> targetClass) {
        Constructor<? extends Action> constructor = PERMISSIONS_CONSTRUCTOR.get(targetClass);
        if (constructor != null) {
            return constructor;
        }
        if (targetClass.getName().equals(Object.class.getName()))
            return null;
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
