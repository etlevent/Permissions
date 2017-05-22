package cherry.android.permissions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import cherry.android.permissions.base.BaseActivity;

/**
 * Created by Administrator on 2017/5/18.
 */

public class FragmentActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        findViewById(R.id.button1).setOnClickListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FirstFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        //method1();
        startActivity(new Intent(this, SecondFragmentActivity.class));
        finish();
    }

//    @RequestPermission(value = Manifest.permission.CALL_PHONE, requestCode = 1111)
//    void method1() {
//        Log.i("Test", "fragment act request");
//    }
//
//    @PermissionGranted(1111)
//    void method2() {
//        Log.i("Test", "fragment act grant");
//    }
//
//    @PermissionDenied(1111)
//    void method3() {
//        Log.i("Test", "fragment act denied");
//    }
}
