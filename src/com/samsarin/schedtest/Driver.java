package com.samsarin.schedtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Driver {
    private static final Random RANDOM = new Random();

    private static final int INITIAL_ARRAY_SIZE = 10;
    private static final int MAX_ARRAY_SIZE = 10 * 1000 * 1000;
    private static final int STEP_FACTOR = 10;
    
    private static final long POST_GC_SLEEP_MILLIS = 10;
    private static final long ITERS_PER_COMBINATION = 100;
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final MergeSort[] impls = new MergeSort[] {
                new SerialMergeSort(),
                new ThreadPoolMergeSort(),
                new CallbackMergeSort(),
                new ForkJoinMergeSort()
        };

        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = INITIAL_ARRAY_SIZE; i <= MAX_ARRAY_SIZE; i *= STEP_FACTOR) {
            sizes.add(i);
        }

        final long[][] results = new long[impls.length][sizes.size()];
        for (int i = 0; i < sizes.size(); i++) {
            final int size = sizes.get(i);
            System.out.println("Sorting array of length: " + size);
            final int[] toSort = createRandomArray(size);
            for (int j = 0; j < impls.length; j++) {
                final MergeSort impl = impls[j];
                System.out.printf("    Testing implementation: %20s ", impl.getClass().getSimpleName());
                results[j][i] = Long.MAX_VALUE;
                for (int k = 0; k < ITERS_PER_COMBINATION; k++) {
                    gc();
                    impl.before();
                    final long startTime = System.nanoTime();
                    try {
                        impls[j].mergesort(toSort, 0, toSort.length);
                        final long elapsedTime = System.nanoTime() - startTime;
                        results[j][i] = Math.min(results[j][i], elapsedTime);
                        System.out.print(".");
                    } catch (Throwable t) {
                        System.out.print("!");
                    }
                    impl.after();
                    gc();
                }
                System.out.println();
            }
        }

        System.out.println("Done");
        System.out.println();

        // Print header
        System.out.printf("%-20s", "Strategy");
        for (int size : sizes) {
            System.out.printf(" %12d", size);
        }
        System.out.println();
        
        // Now print results
        for (int i = 0; i < impls.length; i++) {
            System.out.printf("%-20.20s", impls[i].getClass().getSimpleName());
            for (int j = 0; j < sizes.size(); j++) {
                long result = results[i][j];
                if (result != Long.MAX_VALUE) {
                    System.out.printf(" %12d", result);
                } else {
                    System.out.printf(" %12s", "ERROR");
                }
            }
            System.out.println();
        }
    }
    
    private static void gc() {
        System.gc();
        try {
            Thread.sleep(POST_GC_SLEEP_MILLIS);
        } catch (InterruptedException e) {
            // Should never happen, but we re-interrupt to honor the contract.
            Thread.currentThread().interrupt();
        }
    }

    private static int[] createRandomArray(int size) {
        final int[] arr = new int[size];
        for (int i = 0; i < size; i++)
            arr[i] = RANDOM.nextInt();
        return arr;
    }
}
