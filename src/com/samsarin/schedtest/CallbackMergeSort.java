package com.samsarin.schedtest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class CallbackMergeSort implements MergeSort {
    private volatile ExecutorService executor;
    
    @Override
    public int[] mergesort(int[] array, int from, int to) throws ExecutionException, InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        final AtomicReference<int[]> resultRef = new AtomicReference<int[]>();
        final Callback callback = new Callback() {
            @Override
            public void invoke(int[] result) {
                resultRef.set(result);
                cdl.countDown();
            }
        };
        new MergesortRunnable(array, from, to, callback).run();
        cdl.await();
        return resultRef.get();
    }
    
    private class MergesortRunnable implements Runnable {
        private final int[] array;
        private final int from;
        private final int to;
        private final Callback callback;

        private MergesortRunnable(int[] array, int from, int to, Callback callback) {
            this.array = array;
            this.from = from;
            this.to = to;
            this.callback = callback;
        }

        @Override
        public void run() {
            if (to - from <= Util.TRIVIAL_SIZE)
                callback.invoke(Util.trivialSort(array, from, to));
            else {
                final int midpoint = (to - from) / 2 + from;

                final AtomicReference<int[]> fstRef = new AtomicReference<int[]>();
                final Callback childCallback = new Callback() {
                    @Override
                    public void invoke(int[] result) {
                        if (!fstRef.compareAndSet(null, result)) {
                            callback.invoke(Util.merge(fstRef.get(), result));
                        }
                    }
                };
                
                executor.execute(new MergesortRunnable(array, from, midpoint, childCallback));
                new MergesortRunnable(array, midpoint, to, childCallback).run();
            }
        }
    }
    
    @Override
    public void before() {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void after() {
        this.executor.shutdownNow();
    }
    
    private static interface Callback {
        void invoke(int[] result);
    }
}
