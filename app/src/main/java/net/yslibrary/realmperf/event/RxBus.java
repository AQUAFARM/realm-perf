package net.yslibrary.realmperf.event;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by shimizu_yasuhiro on 2016/05/12.
 */
public class RxBus {

    private final SerializedSubject<Event, Event> mBus = new SerializedSubject<>(
            PublishSubject.create());

    public void emit(Event event) {
        mBus.onNext(event);
    }

    public Observable<Event> asObservable() {
        return mBus.asObservable();
    }

    public <R extends Event> Observable<R> on(Class<R> eventType) {
        return mBus.ofType(eventType);
    }

    public <R extends Event> Observable<R> on(Class<R> eventType, R initialValue) {
        return mBus.ofType(eventType).startWith(initialValue);
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    /**
     * empty interface to restrict RxBus' value
     */
    public interface Event {

    }
}
