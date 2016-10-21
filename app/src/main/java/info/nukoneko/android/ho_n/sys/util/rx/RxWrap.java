package info.nukoneko.android.ho_n.sys.util.rx;

import android.app.ProgressDialog;

import com.trello.rxlifecycle.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

import info.nukoneko.android.ho_n.sys.eventbus.NKEvent;
import info.nukoneko.android.ho_n.sys.eventbus.NKEventBusProvider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by atsumi on 2016/10/20.
 */

/**
 * create observable for android.
 * if calling from "extends BaseActivity, BaseFragment...",
 *  usable ".compose(usable bindToLifecycle())"
 */
public final class RxWrap {
    private RxWrap(){}

    public static <T> Observable<T> create(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> create(Observable<T> observable, ProgressDialog progressDialog) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(progressDialog::show)
                .doOnCompleted(progressDialog::dismiss);
    }

    public static Observable<NKEvent> eventCreate(Observable.Transformer<NKEvent, NKEvent> objectTransformer) {
        return NKEventBusProvider.getInstance().toObservable()
                .compose(objectTransformer)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
