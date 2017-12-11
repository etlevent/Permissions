package cherry.android.permissions.api.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import cherry.android.permissions.api.Permissions;

/**
 * Created by roothost on 2017/12/11.
 */

public class PermissionFragmentV4 extends Fragment {

    private static final String TAG = "Permission";

    private Object target;

    private StringBuilder mBuilder;

    public void setTarget(@NonNull Object target) {
        this.target = target;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBuilder = new StringBuilder();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        logMessage(permissions);
        if (PermissionUtils.hasSelfPermissions(PermissionUtils.getContext(target), permissions)) {
            Permissions.permissionGranted(target, requestCode);
        } else {
            Permissions.permissionDenied(target, requestCode, permissions);
        }
    }

    private void logMessage(@NonNull String[] permissions) {
        if (mBuilder.length() > 0) {
            mBuilder.delete(0, mBuilder.length());
        }
        mBuilder.append("v4 onRequestPermissionsResult:\n")
                .append("permissions=[");
        for (String permission : permissions) {
            mBuilder.append(permission)
                    .append('\t');
        }
        mBuilder.append("];\n")
                .append("target=")
                .append(target);
        Log.d(TAG, mBuilder.toString());
    }
}
