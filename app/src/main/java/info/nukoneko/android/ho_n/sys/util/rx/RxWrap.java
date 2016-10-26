package info.nukoneko.android.ho_n.sys.util.rx;

import android.app.ProgressDialog;

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

    private static <T> Observable<T> createBase(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> create(Observable<T> observable) {
        return createBase(observable);
    }

    public static <T> Observable<T> create(Observable<T> observable, ProgressDialog progressDialog) {
        return createBase(observable)
                .doOnSubscribe(progressDialog::show)
                .doOnCompleted(progressDialog::dismiss);
    }

    public static <T> Observable<T> create(Observable<T> observable,
                                           ProgressDialog progressDialog,
                                           Observable.Transformer<T, T> objectTransformer) {
        return createBase(observable.compose(objectTransformer))
                .doOnSubscribe(progressDialog::show)
                .doOnCompleted(progressDialog::dismiss);
    }

    public static Observable<NKEvent> eventReceive(Observable.Transformer<NKEvent, NKEvent> objectTransformer) {
        return NKEventBusProvider.getInstance().toObservable()
                .compose(objectTransformer)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> create(Observable<T> observable, Observable.Transformer<T, T> objectTransformer) {
        return observable
                .compose(objectTransformer)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
