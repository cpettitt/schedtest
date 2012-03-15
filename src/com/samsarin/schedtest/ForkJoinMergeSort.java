package com.samsarin.schedtest;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinMergeSort implements MergeSort {
    private ForkJoinPool _pool;
    
    @Override
    public int[] mergesort(int[] array, int from, int to) {
        return _pool.invoke(new MergeSortTask(array, from, to));
    }
    
    @Override
    public void before() {
        _pool = new ForkJoinPool();
    }

    @Override
    public void after() {
        _pool.shutdownNow();
    }
    
    private static class MergeSortTask extends RecursiveTask<int[]> {
        private final int[] array;
        private final int from;
        private final int to;
        
        public MergeSortTask(int[] array, int from, int to) {
            this.array = array;
            this.from = from;
            this.to = to;
        }

        @Override
        protected int[] compute() {
            if (to - from <= Util.TRIVIAL_SIZE)
                return Util.trivialSort(array, from, to);

            final int midpoint = (to - from) / 2 + from;
            final MergeSortTask fst = new MergeSortTask(array, from, midpoint);
            fst.fork();
            final MergeSortTask snd = new MergeSortTask(array, midpoint, to);
            return Util.merge(snd.compute(), fst.join());
        }
    }
}
