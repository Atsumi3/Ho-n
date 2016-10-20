package info.nukoneko.android.ho_n.sys.base;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class BaseActivity extends RxAppCompatActivity {
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }
}
