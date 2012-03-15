package com.samsarin.schedtest;

import java.util.concurrent.ExecutionException;

public interface MergeSort {
    int[] mergesort(int[] array, int from, int to) throws ExecutionException, InterruptedException;

    void before();

    void after();
}
