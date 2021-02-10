package com.technosales.net.buslocationannouncement.printlib;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {
    private static CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    /**
     * this method actually executes the runnable in main thread which is not expected
     * @param runnable
     */
    @Deprecated
    public static void runInThread(final Runnable runnable) {
        mCompositeDisposable.add(Observable.just(0).compose(RxUtils.<Integer>ioThread()).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                runnable.run();
            }
        }));
    }

    /**
     * execute a runnable in new thread
     * @param runnable the runnable to be executed in new thread
     */
    public static void runInBackgroud(final Runnable runnable) {
        mCompositeDisposable.add(Observable.just(0).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                runnable.run();
            }
        }));
    }


    public static <T> ObservableTransformer<T, T> ioThread() {
        return new ObservableTransformer<T, T>() {

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread());
                return upstream;
            }
        };
    }

    public static <T> ObservableTransformer<T, T> ioMain() {
        return new ObservableTransformer<T, T>() {

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                return upstream;
            }
        };
    }


    public static void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    public static void release() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }
}
