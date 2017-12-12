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
        startActivity(new Intent(this, SecondFragmentActivity.class));
        finish();
    }

}
