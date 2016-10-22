package info.nukoneko.android.ho_n.sys.eventbus;

import android.util.Log;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKEventBus {
    private final Subject<NKEvent, NKEvent> mBus =
            new SerializedSubject<>(PublishSubject.create());

    public NKEventBus() {
    }

    public void send(NKEvent o) {
        Log.d("Event Send", o.toString());
        mBus.onNext(o);
    }

    public void sendTwi(NKEventTwitter o) {
        Log.d("Event SendTwi", o.toString());
        mBus.onNext(o);
    }

    public Observable<NKEvent> toObservable() {
        return mBus;
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }
}
