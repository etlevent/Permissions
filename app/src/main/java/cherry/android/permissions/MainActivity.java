package cherry.android.permissions;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cherry.android.permissions.annotations.PermissionDenied;
import cherry.android.permissions.annotations.PermissionGranted;
import cherry.android.permissions.annotations.RequestPermission;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        testRequestPermission();
    }

    @RequestPermission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 1001)
    void testRequestPermission() {
        Log.d("Test", "testRequestPermission");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGranted(1001)
    void permissionGranted() {

    }

    @PermissionDenied(1001)
    void permissionDenied() {

    }
}
