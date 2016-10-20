package info.nukoneko.android.ho_n.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxDialogFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import info.nukoneko.android.ho_n.util.rx.Optional;
import rx.functions.Action1;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class BaseDialogFragment extends RxDialogFragment {

    private Unbinder mUnBinder;

    @LayoutRes
    abstract public int dialogLayoutId();

    @NonNull
    abstract public Dialog dialogSetup(Dialog dialog);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialogSetup(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(dialogLayoutId(), container, false);
        mUnBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Optional.ofNullable(mUnBinder).subscribe(Unbinder::unbind);
    }
}
