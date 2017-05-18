package cherry.android.permissions;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import cherry.android.permissions.annotations.PermissionDenied;
import cherry.android.permissions.annotations.PermissionGranted;
import cherry.android.permissions.annotations.RequestPermission;

/**
 * Created by Administrator on 2017/5/18.
 */

public class FragmentActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        findViewById(R.id.button1).setOnClickListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FirstFragment()).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        method1();
    }

    @RequestPermission(value = Manifest.permission.CALL_PHONE, requestCode = 1111)
    void method1() {
        Log.i("Test", "fragment act request");
    }

    @PermissionGranted(1111)
    void method2() {
        Log.i("Test", "fragment act grant");
    }

    @PermissionDenied(1111)
    void method3() {
        Log.i("Test", "fragment act denied");
    }
}
