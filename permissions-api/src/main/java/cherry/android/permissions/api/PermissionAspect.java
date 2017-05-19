package cherry.android.permissions.api;

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
        if (!PermissionUtils.hasSelfPermissions(PermissionUtils.getContext(target), permissions)) {
            Log.i(TAG, "requestPermissions " + buildPermissionMessage(permissions, requestCode));
            PermissionUtils.requestPermissions(target, permissions, requestCode);
            return null;
        } else {
            return joinPoint.proceed();
        }
    }

    @Around("execution(* *.onRequestPermissionsResult(..))")
    public Object requestPermissionsRequest(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        int requestCode = (int) args[0];
        String[] permissions = (String[]) args[1];
        Log.i(TAG, "requestPermissionsResult " + requestCode + ",permissions:" + permissions[0]);
        Object target = joinPoint.getTarget();
        if (PermissionUtils.hasSelfPermissions(PermissionUtils.getContext(target), permissions)) {
            PermissionUtils.permissionGranted(target, requestCode);
        } else {
            PermissionUtils.permissionDenied(target, requestCode, permissions);
        }
        return result;
    }

    private static String buildPermissionMessage(String[] permissions, int code) {
        StringBuilder builder = new StringBuilder();
        builder.append("PermissionUtils:[\n");
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


//    within(android.app.Activity+)
//    @Before("execution(* android.app.Activity.on**(..))")
//    @Before("within(android.app.Activity+) || target(android.app.Activity)" +
//            " || target(android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback)")
//    public void testOn(JoinPoint joinPoint) {
//        Log.e(TAG, "test on " + joinPoint.getSignature());
//    }

//        private static final String POINTCUT_ACTIVITY_PERMISSIONS_RESULT_METHOD =
//            "execution(* android.app.Activity+.onRequestPermissionsResult(..))" +
//                    " || execution(* android.app.Activity.onRequestPermissionsResult(..))";
//    private static final String POINTCUT_FRAGMENT_ACTIVITY_PERMISSIONS_RESULT_METHOD =
//            "execution(* android.support.v4.app.FragmentActivity+.onRequestPermissionsResult(..))" +
//                    " || execution(* android.support.v4.app.FragmentActivity.onRequestPermissionsResult(..))";
//    private static final String POINTCUT_FRAGMENT_PERMISSIONS_RESULT_METHOD =
//            "execution(* android.app.Fragment+.onRequestPermissionsResult(..))" +
//                    " || execution(* android.app.Fragment.onRequestPermissionsResult(..))";
//    private static final String POINTCUT_SUPPORT_FRAGMENT_PERMISSIONS_RESULT_METHOD =
//            "execution(* android.support.v4.app.Fragment+.onRequestPermissionsResult(..))" +
//                    " || execution(* android.support.v4.app.Fragment.onRequestPermissionsResult(..))";
//
//    //args
//    @Pointcut(POINTCUT_ACTIVITY_PERMISSIONS_RESULT_METHOD)
//    public void activityPermissionsResult() {
//    }
//
//    @Pointcut(POINTCUT_FRAGMENT_ACTIVITY_PERMISSIONS_RESULT_METHOD)
//    public void fragmentActivityPermissionsResult() {
//    }
//
//    @Pointcut(POINTCUT_FRAGMENT_PERMISSIONS_RESULT_METHOD)
//    public void fragmentPermissionsResult() {
//    }
//
//    @Pointcut(POINTCUT_SUPPORT_FRAGMENT_PERMISSIONS_RESULT_METHOD)
//    public void supportFragmentPermissionsResult() {
//    }
//        @Around("activityPermissionsResult()" +
//            " || fragmentActivityPermissionsResult()" +
//            " || fragmentPermissionsResult()" +
//            " || supportFragmentPermissionsResult()")
}
