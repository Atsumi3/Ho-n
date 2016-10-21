package info.nukoneko.android.ho_n.sys.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class BaseFragment extends RxFragment {

    private Unbinder mUnBinder;

    @LayoutRes
    abstract public int fragmentLayoutId();

    abstract public void fragmentSetup(Bundle bundle);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(fragmentLayoutId(), container, false);
        mUnBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("ViewCreated");
        fragmentSetup(getArguments());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Optional.ofNullable(mUnBinder).subscribe(Unbinder::unbind);
    }
}
