package info.nukoneko.android.ho_n.sys.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxDialogFragment;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class BaseDialogFragment extends RxDialogFragment {
    @LayoutRes
    abstract public int dialogLayoutId();

    @NonNull
    abstract public Dialog dialogSetup(Dialog dialog);

    abstract public void dialogSetupParams(Bundle bundle);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialogSetup(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(dialogLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogSetupParams(getArguments());
    }
}
