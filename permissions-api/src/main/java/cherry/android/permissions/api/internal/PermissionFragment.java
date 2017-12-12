package cherry.android.permissions.api.internal;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import cherry.android.permissions.api.Permissions;

/**
 * Created by roothost on 2017/12/11.
 */

public class PermissionFragment extends Fragment {

    private static final String TAG = "Permission";

    private Permissions mPermissions;

    private StringBuilder mBuilder;

    public void setTarget(@NonNull Object target) {
        mPermissions = new Permissions(target);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBuilder = new StringBuilder();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Permissions onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Permissions onPause");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        logMessage(permissions);
        if (PermissionUtils.hasSelfPermissions(getActivity(), permissions)) {
            mPermissions.granted(requestCode);
        } else {
            mPermissions.denied(requestCode, permissions);
        }
    }

    private void logMessage(@NonNull String[] permissions) {
        if (mBuilder.length() > 0) {
            mBuilder.delete(0, mBuilder.length());
        }
        mBuilder.append("onRequestPermissionsResult:\n")
                .append("permissions=[")
                .append(TextUtils.join(",", permissions))
                .append("];\n")
                .append("target=")
                .append(mPermissions.getTarget());
        Log.d(TAG, mBuilder.toString());
    }
}
