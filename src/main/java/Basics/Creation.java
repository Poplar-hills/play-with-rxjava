package Basics;


import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static Basics.Helper.log;

public class Creation {
    public static void just() {
        Observable.just("@@", "##")
            .subscribe(x -> log(x));
    }

    public static void interval() {
        Observable.interval(1, TimeUnit.SECONDS)
            .take(10)
            .subscribe(x -> log(x));
    }

    public static void range() {
        Observable.range(1, 5)
                .subscribe(x -> log(x));
    }

    public static void fromArray() {
        Observable.fromArray(1, 2, 3)  // fromArray 的参数并不是 array
                .subscribe(x -> log(x));
    }

    public static void fromIterable() {
        List<String> list = Arrays.asList("a", "b", "c");
        Observable.fromIterable(list)
                .subscribe(x -> log(x));
    }

    public static void fromCallable() {
        Callable<String> callable = () -> "Callable invoked";
        Single.fromCallable(callable)  // 注意这里最好用 Single 而不是 Observable，因为一个 callable 只会被调用一次就 complete
                .subscribe(x -> log(x), e -> log(e.getMessage()));  // 注意 Single 的 subscribe 没有 onComplete 参数
    }

    public static void fromRunnable() {
        Runnable runnable = () -> { log("Runnable invoked"); };
        Completable.fromRunnable(runnable)  // 注意这里用的是 Completable，因为 runnable 没有返回值，所以只需要 complete 或者 error，不会 next
                .subscribe(() -> { log("fromRunnable complete"); });
    }

    public static void fromFuture() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            Thread.sleep(1000);
            return "1 second passed";
        });

        Single.fromFuture(future)
                .subscribe(x -> log(x));
    }

    public static void create() {
        Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                for (int n : new int[]{1, 2, 3, 4, 5}) {
                    if (n > 3) {
                        emitter.onError(new IllegalStateException("Oops!"));
                        break;
                    }
                    emitter.onNext(n);
                }
            }
        })
                .subscribe(x -> log(x), Throwable::printStackTrace);
    }

    public static void generate() {
        int startValue = 10;
        int incrementValue = 1;

        Observable.generate(() -> startValue, (state, emitter) -> {
            int nextValue = state + incrementValue;
            emitter.onNext(nextValue);
            return nextValue;
        })
                .take(10)
                .subscribe(x -> log(x));
    }

    public static void main(String[] args) {
        just();
        interval();
        range();
        fromArray();
        fromIterable();
        fromCallable();
        fromRunnable();
        fromFuture();
        create();
        generate();
    }
}
