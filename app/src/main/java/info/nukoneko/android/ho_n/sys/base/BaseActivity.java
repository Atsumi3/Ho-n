package info.nukoneko.android.ho_n.sys.base;

import android.support.annotation.LayoutRes;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class BaseActivity extends RxAppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }
}
