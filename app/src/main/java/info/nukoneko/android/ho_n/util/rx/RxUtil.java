package info.nukoneko.android.ho_n.util.rx;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class RxUtil {
    private RxUtil(){}

    public interface RxCallable<T> {
        T call() throws Exception;
    }

    public static <T> Observable<T> createObservable(RxCallable<T> observable) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(observable.call());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static <T> T head(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return Observable.from(list).toBlocking().first();
    }

    public static <T> List<T> tail(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return Observable.from(list).skip(1).toList().toBlocking().single();
    }

    public static <T> boolean indexOf(List<T> list, @NonNull T target) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        return Observable.from(list)
                .map(t -> t.equals(target))
                .filter(aBoolean -> aBoolean)
                .toBlocking().single();
    }
}
