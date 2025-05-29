package com.noureddine.library.utils;

import java.math.BigInteger;
import java.util.List;

public class Utils {
    //swap method to swap to elements in a list
    public static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
    //compare strings  method to compare strings even with numbers in them
    public static int compareStrings(String a, String b) {
        int i = 0, j = 0;
        int lenA = a.length(), lenB = b.length();
        //compare the Strings char by char
        while (i < lenA && j < lenB) {
            char chA = a.charAt(i);
            char chB = b.charAt(j);
            //if the compared chars are both digits then get the full digits after and compare as a hole number
            if (Character.isDigit(chA) && Character.isDigit(chB)) {
                //extract full number from a
                int startA = i;
                while (i < lenA && Character.isDigit(a.charAt(i))) i++;
                String numStrA = a.substring(startA, i);

                //extract full number from b
                int startB = j;
                while (j < lenB && Character.isDigit(b.charAt(j))) j++;
                String numStrB = b.substring(startB, j);

                // Compare numbers as integers (handle large numbers too)
                int cmp = new BigInteger(numStrA).compareTo(new BigInteger(numStrB));
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
