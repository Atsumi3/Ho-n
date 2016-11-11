package info.nukoneko.android.ho_n.sys.eventbus;

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
