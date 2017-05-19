package cherry.android.permissions;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cherry.android.permissions.annotations.PermissionDenied;
import cherry.android.permissions.annotations.PermissionGranted;
import cherry.android.permissions.annotations.PermissionNeverAskAgain;
import cherry.android.permissions.annotations.RequestPermission;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        Toast.makeText(this, "permissionGranted", Toast.LENGTH_SHORT).show();
        testRequestPermission();
    }

    @PermissionDenied(1001)
    void permissionDenied() {
        Toast.makeText(this, "permissionDenied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionNeverAskAgain(1001)
    void permissionNeverAskAgain() {
        Toast.makeText(this, "permissionGranted", Toast.LENGTH_SHORT).show();
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
