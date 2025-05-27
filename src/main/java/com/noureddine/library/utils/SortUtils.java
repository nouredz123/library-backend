package com.noureddine.library.utils;

import java.util.List;
import java.util.function.Function;

public class SortUtils {
    //overloaded method with default 'false' for descending
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
            swap(list, 0, i);
            //call heapify on the heap without the last element
            heapify(list, i, 0, keyExtractor, descending);
        }
    }

    private static <T, K extends Comparable<K>> void heapify(List<T> list, int heapSize, int rootIndex, Function<T, K> keyExtractor, boolean descending) {
        int extreme = rootIndex;
        int left = 2 * rootIndex + 1;
        int right = 2 * rootIndex + 2;

        if (left < heapSize) {
            K leftKey = keyExtractor.apply(list.get(left));
            K extremeKey = keyExtractor.apply(list.get(extreme));

            int comparison;

            if (leftKey instanceof String && extremeKey instanceof String) {
                comparison = naturalCompare((String) leftKey, (String) extremeKey);
            } else {
                comparison = leftKey.compareTo(extremeKey);
            }

            if (descending) {
                if (comparison < 0) {
                    extreme = left;
                }
            } else {
                if (comparison > 0) {
                    extreme = left;
                }
            }
        }


        if (right < heapSize) {
            K rightKey = keyExtractor.apply(list.get(right));
            K extremeKey = keyExtractor.apply(list.get(extreme));

            int comparison;

            if (rightKey instanceof String && extremeKey instanceof String) {
                comparison = naturalCompare((String) rightKey, (String) extremeKey);
            } else {
                comparison = rightKey.compareTo(extremeKey);
            }
            if (descending) {
                if (comparison < 0) {
                    extreme = right;
                }
            } else {
                if (comparison > 0) {
                    extreme = right;
                }
            }
        }

        if (extreme  != rootIndex) {
            swap(list, rootIndex, extreme );
            heapify(list, heapSize, extreme , keyExtractor ,descending);
        }
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    private static int naturalCompare(String a, String b) {
        int i = 0, j = 0;
        int lenA = a.length(), lenB = b.length();

        while (i < lenA && j < lenB) {
            char chA = a.charAt(i);
            char chB = b.charAt(j);

            if (Character.isDigit(chA) && Character.isDigit(chB)) {
                // Extract full number from a
                int startA = i;
                while (i < lenA && Character.isDigit(a.charAt(i))) i++;
                String numStrA = a.substring(startA, i);

                // Extract full number from b
                int startB = j;
                while (j < lenB && Character.isDigit(b.charAt(j))) j++;
                String numStrB = b.substring(startB, j);

                // Compare numbers as integers (handle large numbers too)
                int cmp = new java.math.BigInteger(numStrA).compareTo(new java.math.BigInteger(numStrB));
                if (cmp != 0) return cmp;
            } else {
                // Compare characters (case insensitive like String::compareToIgnoreCase)
                int cmp = Character.compare(Character.toLowerCase(chA), Character.toLowerCase(chB));
                if (cmp != 0) return cmp;
                i++;
                j++;
            }
        }

        // One of the strings may have more characters
        return Integer.compare(lenA - i, lenB - j);
    }
}
