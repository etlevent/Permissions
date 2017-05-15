package permission.api;

/**
 * Created by Administrator on 2017/5/15.
 */

public interface Action {
    boolean checkPermissions(String[] permissions);

    void permissionGranted(int requestCode);

    void permissionDenied(int requestCode);
}
