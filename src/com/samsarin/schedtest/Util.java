package com.samsarin.schedtest;

import java.util.Arrays;

public class Util {
    public static int TRIVIAL_SIZE = 256;
    
    public static int[] merge(int[] fst, int[] snd) {
        final int[] result = new int[fst.length + snd.length];
        for (int r = 0, f = 0, s = 0; r < result.length; r++) {
            if (f == fst.length)
                result[r] = snd[s++];
            else if (s == snd.length)
                result[r] = fst[f++];
            else {
                result[r] = fst[f] < snd[s]
                        ? fst[f++]
                        : snd[s++];
            }
        }
        return result;
    }
    
    public static int[] trivialSort(int[] array, int from, int to) {
        final int[] sorted = Arrays.copyOfRange(array, from, to);
        Arrays.sort(sorted);
        return sorted;
    }
}
