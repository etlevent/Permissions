package cherry.android.permissions.api;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import cherry.android.permissions.annotations.RequestPermission;

/**
 * Created by Administrator on 2017/5/17.
 */
@Aspect
public class PermissionAspect {

    private static final String TAG = "PermissionAspect";

    private static final String REQUEST_PERMISSION_POINTCUT_METHOD =
            "execution(@com.example.RequestPermission * *(..))";
    private static final String REQUEST_PERMISSION_POINTCUT_CONSTRUCTOR =
            "execution(@com.example.RequestPermission *.new(..))";

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
        Object target = joinPoint.getTarget();
        if (!Permissions.hasSelfPermissions(Permissions.getContext(target), permissions)) {
            Log.i(TAG, "requestPermissions " + buildPermissionMessage(permissions, requestCode));
            Permissions.requestPermissions(target, permissions, requestCode);
            return null;
        } else {
            return joinPoint.proceed();
        }
    }

    //onRequestPermissionsResult
    @Around("execution(* *.onRequestPermissionsResult(..))")
    public Object requestPermissionsRequest(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        int requestCode = (int) args[0];
        String[] permissions = (String[]) args[1];
        Log.i(TAG, "requestPermissionsResult " + requestCode + ",permissions:" + permissions[0]);
        Object target = joinPoint.getTarget();
        if (Permissions.hasSelfPermissions(Permissions.getContext(target), permissions)) {
            Permissions.permissionGranted(target, requestCode);
        } else {
            Permissions.permissionDenied(target, requestCode);
        }
        return result;
    }

    private static String buildPermissionMessage(String[] permissions, int code) {
        StringBuilder builder = new StringBuilder();
        builder.append("Permissions:[\n");
        for (String permission : permissions) {
            builder.append("\t\t")
                    .append(permission)
                    .append(",\n");
        }
        builder.append("],\n")
                .append("requestCode=" + code)
                .append("\n");
        return builder.toString();
    }

}
