package com.samsarin.schedtest;

import java.util.concurrent.*;

public class ThreadPoolMergeSort implements MergeSort {
    private volatile ExecutorService executor;

    @Override
    public int[] mergesort(final int[] array, final int from, final int to) throws ExecutionException, InterruptedException {
        if (to - from <= Util.TRIVIAL_SIZE)
            return Util.trivialSort(array, from, to);

        final int midpoint = (to - from) / 2 + from;

        final FutureTask<int[]> fstFuture = new FutureTask<int[]>(new Callable<int[]>() {
            @Override
            public int[] call() throws Exception {
                return mergesort(array, from, midpoint);
            }
        });
        executor.execute(fstFuture);
        
        final int[] snd = mergesort(array, midpoint, to);
        return Util.merge(fstFuture.get(), snd);
    }

    @Override
    public void before() {
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void after() {
        this.executor.shutdownNow();
    }
}
