package cherry.android.permissions.api;

import android.text.TextUtils;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import cherry.android.permissions.annotations.RequestPermission;
import cherry.android.permissions.api.internal.PermissionUtils;

/**
 * Created by Administrator on 2017/5/17.
 */
@Aspect
public class PermissionAspect {

    private static final String TAG = "PermissionAspect";

    private static final String REQUEST_PERMISSION_POINTCUT_METHOD =
            "execution(@cherry.android.permissions.annotations.RequestPermission * *(..))";
    private static final String REQUEST_PERMISSION_POINTCUT_CONSTRUCTOR =
            "execution(@cherry.android.permissions.annotations.RequestPermission *.new(..))";

    @Pointcut(REQUEST_PERMISSION_POINTCUT_METHOD + " && @annotation(args)")
    public void requestPermissionMethod(RequestPermission args) {
    }

    @Pointcut(REQUEST_PERMISSION_POINTCUT_CONSTRUCTOR + " && @annotation(args)")
    public void requestPermissionConstructor(RequestPermission args) {
    }

    @Around("requestPermissionMethod(requestPermission) || requestPermissionConstructor(requestPermission)")
    public Object requestPermissions(final ProceedingJoinPoint joinPoint, RequestPermission requestPermission) throws Throwable {
        String[] permissions = requestPermission.value();
        int requestCode = requestPermission.requestCode();
        final Object target = joinPoint.getTarget();
        Log.i(TAG, "requestPermissions " + buildPermissionMessage(permissions, requestCode));
        if (!PermissionUtils.hasSelfPermissions(PermissionUtils.getContext(target), permissions)) {
            PermissionUtils.requestPermissions(target, permissions, requestCode);
            return null;
        } else {
            return joinPoint.proceed();
        }
    }

    private static String buildPermissionMessage(String[] permissions, int code) {
        return new StringBuilder()
                .append("PermissionUtils:\n[")
                .append(TextUtils.join(",", permissions))
                .append("];\n")
                .append("requestCode=" + code)
                .append("\n")
                .toString();
    }
}
