package cherry.permissions;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import permission.annotation.OnPermissionDenied;
import permission.annotation.OnPermissionGranted;
import permission.api.Permissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnPermissionGranted({102, 101})
    void method1() {
        Toast.makeText(this, "permission granted 1", Toast.LENGTH_SHORT).show();
    }

    @OnPermissionGranted(105)
    void method3() {
        Toast.makeText(this, "permission granted 3", Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied(101)
    void method2() {
        Toast.makeText(this, "permission denied 101", Toast.LENGTH_SHORT).show();
    }
    @OnPermissionDenied(102)
    void method4() {
        Toast.makeText(this, "permission denied 102", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                Permissions.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
                break;
            case R.id.button2:
                Permissions.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                break;
        }
    }
}
