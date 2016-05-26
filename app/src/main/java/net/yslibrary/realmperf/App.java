package net.yslibrary.realmperf;

import net.yslibrary.realmperf.event.DataPrepared;
import net.yslibrary.realmperf.event.RxBus;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by shimizu_yasuhiro on 2016/05/24.
 */
public class App extends Application {

    public static final String LOREM_1
            = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    public static final String LOREM_2
            = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";

    public final AtomicLong noteIdCounter = new AtomicLong(0);

    public final AtomicLong folderIdCounter = new AtomicLong(0);

    public final AtomicLong realmListActivityCount = new AtomicLong(0);

    public final AtomicLong listActivityCount = new AtomicLong(0);

    public final RxBus bus = new RxBus();

    private final boolean shouldInsert = false;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        initRealm();
    }

    public void initRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

        Realm realm = Realm.getDefaultInstance();

        // get latest id
        if (realm.where(Note.class).count() > 0) {
            noteIdCounter.set(realm.where(Note.class).findAllSorted("id").last().id + 1);
        }
        if (realm.where(Folder.class).count() > 0) {
            folderIdCounter.set(realm.where(Folder.class).findAllSorted("id").last().id + 1);
        }
        realm.close();

        if (!shouldInsert) {
            Observable.timer(2, TimeUnit.SECONDS)
                    .subscribe(aLong -> {
                        bus.emit(new DataPrepared(0));
                    });
            return;
        }
        Timber.d("create initial data - from note id: %d", noteIdCounter.get());
        long start = System.currentTimeMillis();
        Observable.range(0, 10000)
                .subscribeOn(Schedulers.io())
                .map(integer -> new Note(noteIdCounter.getAndIncrement(), LOREM_1, LOREM_2,
                        System.currentTimeMillis()))
                .buffer(200)
                .doOnCompleted(() -> {
                    long end = System.currentTimeMillis();
                    long taken = end - start;
                    Timber.d("data creation finished in %d millis", taken);
                    bus.emit(new DataPrepared(taken));
                })
                .subscribe(notes -> {
                    Folder folder = new Folder(folderIdCounter.getAndIncrement(), notes);
                    Realm _realm = Realm.getDefaultInstance();
                    _realm.executeTransaction(realm1 -> {
                        realm1.copyToRealmOrUpdate(folder);
                    });
                    _realm.close();
                });
    }
}
