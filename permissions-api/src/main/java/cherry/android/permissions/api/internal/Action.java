package cherry.android.permissions.api.internal;

/**
 * Created by Administrator on 2017/5/15.
 */

public interface Action {
    void permissionGranted(int requestCode);

    void permissionDenied(int requestCode);

    boolean shouldPermissionRationale(int requestCode);

    void showPermissionRationale(int requestCode);

    void updateTarget(Object target);
}
