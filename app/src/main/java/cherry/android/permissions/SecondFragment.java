package cherry.android.permissions;


import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cherry.android.permissions.annotations.PermissionDenied;
import cherry.android.permissions.annotations.PermissionGranted;
import cherry.android.permissions.annotations.RequestPermission;
import cherry.android.permissions.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button1).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermission();
    }

    @Override
    public void onClick(View v) {
        requestPermission();
    }

    @RequestPermission(value = Manifest.permission.CALL_PHONE, requestCode = 1002)
    void requestPermission() {
        Log.e("Test", "secondFragment requestPermission");
    }

    @PermissionGranted(1002)
    void method1() {
        Log.e("Test", "secondFragment granted");
    }

    @PermissionDenied(1002)
    void method2() {
        Log.e("Test", "secondFragment denied");
    }
}
