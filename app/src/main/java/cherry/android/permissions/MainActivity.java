package cherry.android.permissions;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import cherry.android.permissions.annotations.PermissionDenied;
import cherry.android.permissions.annotations.PermissionGranted;
import cherry.android.permissions.annotations.PermissionNeverAskAgain;
import cherry.android.permissions.annotations.RequestPermission;
import cherry.android.permissions.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "Permissions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            testRequestPermission();
        } else {
            startActivity(new Intent(this, FragmentActivity.class));
        }
    }

    @RequestPermission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 1001)
    void testRequestPermission() {
        Toast.makeText(this, "permission method", Toast.LENGTH_SHORT).show();
    }

    @PermissionGranted(1001)
    void permissionGranted() {
        Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
        testRequestPermission();
    }

    @PermissionDenied(1001)
    void permissionDenied() {
        Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
    }

    @PermissionNeverAskAgain(1001)
    void permissionNeverAskAgain() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }
}
