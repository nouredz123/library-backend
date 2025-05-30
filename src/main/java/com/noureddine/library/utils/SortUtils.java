package com.noureddine.library.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SortUtils {
    //overloaded method with default 'false' for descending to get ascending order
    public static <T, K extends Comparable<K>> void heapSort(List<T> list, Function<T, K> keyExtractor) {
        heapSort(list, keyExtractor, false);
    }
    //heap sort method with generics
    public static <T, K extends Comparable<K>> void heapSort(List<T> list, Function<T, K> keyExtractor, boolean descending) {
        int n = list.size();

        //build heap using heapify method
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i, keyExtractor, descending);
        }

        //one by one moving the max to the end
        for (int i = n - 1; i > 0; i--) {
            //swap the root with the last element (put the max value at the end)
            Utils.swap(list, 0, i);
            //call heapify on the heap without the last element
            heapify(list, i, 0, keyExtractor, descending);
        }
    }
    //heapify method to create a min or max heap based on descending parameter
    private static <T, K extends Comparable<K>> void heapify(List<T> list, int heapSize, int rootIndex, Function<T, K> keyExtractor, boolean descending) {
        int extreme = rootIndex;
        int left = 2 * rootIndex + 1;
        int right = 2 * rootIndex + 2;

        if (left < heapSize) {
            K leftKey = keyExtractor.apply(list.get(left));
            K extremeKey = keyExtractor.apply(list.get(extreme));
            int comparison;

            if (leftKey instanceof String && extremeKey instanceof String) {
                comparison = Utils.compareStrings((String) leftKey, (String) extremeKey);
            } else {
                if (leftKey == null && extremeKey == null) comparison = 0;
                else if (leftKey == null) comparison = descending ? 1 : -1;
                else if (extremeKey == null) comparison = descending ? -1 : 1;
                else comparison = leftKey.compareTo(extremeKey);
            }

            if (descending ? comparison < 0 : comparison > 0) {
                extreme = left;
            }
        }

        if (right < heapSize) {
            K rightKey = keyExtractor.apply(list.get(right));
            K extremeKey = keyExtractor.apply(list.get(extreme));
            int comparison;

            if (rightKey instanceof String && extremeKey instanceof String) {
                comparison = Utils.compareStrings((String) rightKey, (String) extremeKey);
            } else {
                if (rightKey == null && extremeKey == null) comparison = 0;
                else if (rightKey == null) comparison = descending ? 1 : -1;
                else if (extremeKey == null) comparison = descending ? -1 : 1;
                else comparison = rightKey.compareTo(extremeKey);
            }

            if (descending ? comparison < 0 : comparison > 0) {
                extreme = right;
            }
        }

        if (extreme  != rootIndex) {
            Utils.swap(list, rootIndex, extreme );
            heapify(list, heapSize, extreme , keyExtractor ,descending);
        }
    }
    //overloaded method with default 'false' for descending to get ascending order
    public static <T, K extends Comparable<K>> void mergeSort(List<T> list, Function<T, K> keyExtractor) {
        mergeSort(list, keyExtractor, false);
    }
    public static <T, K extends Comparable<K>> void mergeSort(List<T> list, Function<T, K> keyExtractor, boolean descending) {
        if (list.size() <= 1) return;

        int mid = list.size() / 2;
        List<T> left = new ArrayList<>(list.subList(0, mid));
        List<T> right = new ArrayList<>(list.subList(mid, list.size()));

        mergeSort(left, keyExtractor, descending);
        mergeSort(right, keyExtractor, descending);

        merge(list, left, right, keyExtractor, descending);
    }

    private static <T, K extends Comparable<K>> void merge(
            List<T> result,
            List<T> left,
            List<T> right,
            Function<T, K> keyExtractor,
            boolean descending
    ) {
        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            K leftKey = keyExtractor.apply(left.get(i));
            K rightKey = keyExtractor.apply(right.get(j));

            int comparison;
            if (leftKey instanceof String && rightKey instanceof String) {
                comparison = Utils.compareStrings((String) leftKey, (String) rightKey);
            } else {
                if (leftKey == null && rightKey == null) {
                    comparison = 0;
                } else if (leftKey == null) {
                    comparison = descending ? 1 : -1;
                } else if (rightKey == null) {
                    comparison = descending ? -1 : 1;
                } else {
                    comparison = leftKey.compareTo(rightKey);
                }
            }

            if ((descending && comparison > 0) || (!descending && comparison <= 0)) {
                result.set(k++, left.get(i++));
            } else {
                result.set(k++, right.get(j++));
            }
        }

        while (i < left.size()) result.set(k++, left.get(i++));
        while (j < right.size()) result.set(k++, right.get(j++));
    }
}
