package info.nukoneko.android.ho_n.sys.eventbus;

import com.trello.rxlifecycle.LifecycleProvider;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKEventBusProvider {
    private static final NKEventBus BUS = new NKEventBus();

    private NKEventBusProvider() {
    }

    public static NKEventBus getInstance() {
        return BUS;
    }
}
