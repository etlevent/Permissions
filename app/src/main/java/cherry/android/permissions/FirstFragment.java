package cherry.android.permissions;


import android.Manifest;
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
            method1();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Test", " resume getActivity=" + getActivity());
        requestCamera();
    }

    @RequestPermission(value = Manifest.permission.CAMERA, requestCode = 111)
    void requestCamera() {
        Toast.makeText(getActivity(), "requestCamera", Toast.LENGTH_SHORT).show();
    }

    @RequestPermission(value = Manifest.permission.READ_CONTACTS, requestCode = 511)
    void method1() {
        Log.e("Test", "FirstFragment requirePermission");

    }

    @PermissionGranted({511, 111})
    void method2() {
        Log.e("Test", "FirstFragment permission granted");
    }

    @PermissionDenied({511, 111})
    void method3() {
        Log.e("Test", "FirstFragment permission denied");
        Log.e("Test", "getActivity=" + getActivity());
        getActivity().finish();
    }
}
