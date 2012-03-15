package com.samsarin.schedtest;

public class SerialMergeSort implements MergeSort {
    @Override
    public int[] mergesort(final int[] array, final int from, final int to) {
        if (to - from <= Util.TRIVIAL_SIZE)
            return Util.trivialSort(array, from, to);

        final int midpoint = (to - from) / 2 + from;
        final int[] fst = mergesort(array, from, midpoint);
        final int[] snd = mergesort(array, midpoint, to);
        return Util.merge(fst, snd);
    }

    @Override
    public void before() {
    }

    @Override
    public void after() {
    }
}
