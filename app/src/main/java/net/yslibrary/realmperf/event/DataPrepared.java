package net.yslibrary.realmperf.event;

/**
 * Created by shimizu_yasuhiro on 2016/05/24.
 */
public class DataPrepared implements RxBus.Event {

    public final long millis;

    public DataPrepared(long millis) {
        this.millis = millis;
    }
}
