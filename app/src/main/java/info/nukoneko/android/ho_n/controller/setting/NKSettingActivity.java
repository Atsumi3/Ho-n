package info.nukoneko.android.ho_n.controller.setting;import android.os.Bundle;import android.support.annotation.Nullable;import android.view.WindowManager;import info.nukoneko.android.ho_n.sys.base.BaseActivity;/** * Created by TEJNEK on 2016/10/22. */public final class NKSettingActivity extends BaseActivity {    @Override    protected void onCreate(@Nullable Bundle savedInstanceState) {        super.onCreate(savedInstanceState);//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);        getFragmentManager().beginTransaction()                .replace(android.R.id.content, new NKSettingFragment()).commit();    }}