package cherry.android.permissions;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cherry.android.permissions.annotations.PermissionDenied;
import cherry.android.permissions.annotations.PermissionGranted;
import cherry.android.permissions.annotations.PermissionNeverAskAgain;
import cherry.android.permissions.annotations.RequestPermission;
import cherry.android.permissions.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button1).setOnClickListener(this);
        view.findViewById(R.id.button2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button2) {
            getChildFragmentManager().beginTransaction().add(R.id.fragment_container, new SecondFragment()).commit();
        } else {
//            method1();
            requestCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(MainActivity.TAG, " resume getActivity=" + getActivity());
//        requestCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(MainActivity.TAG, "onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(MainActivity.TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(MainActivity.TAG, "onStop");
    }

    @RequestPermission(value = Manifest.permission.CAMERA, requestCode = 111)
    void requestCamera() {
        Toast.makeText(getActivity(), "requestCamera", Toast.LENGTH_SHORT).show();
    }

    @RequestPermission(value = Manifest.permission.READ_CONTACTS, requestCode = 511)
    void method1() {
        Log.e(MainActivity.TAG, "FirstFragment requirePermission");

    }

    @PermissionGranted({511, 111})
    void method2() {
        Log.e(MainActivity.TAG, "FirstFragment permission granted");
    }

    @PermissionDenied({511, 111})
    void method3() {
        Log.e(MainActivity.TAG, "FirstFragment permission denied");
        Log.e(MainActivity.TAG, "getActivity=" + getActivity());
        getActivity().finish();
    }

    @PermissionNeverAskAgain(111)
    void permissionNeverAskAgain() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getActivity().getPackageName());
        }
        startActivity(intent);
    }
}
