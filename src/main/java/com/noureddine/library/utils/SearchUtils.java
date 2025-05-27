package com.noureddine.library.utils;

import java.util.List;
import java.util.function.Function;

public class SearchUtils {
    public static <T, IdType extends Comparable<IdType>> T binarySearch(
            List<T> list,
            IdType targetId,
            Function<T, IdType> idGetter
    ) {
        int left = 0;
        int right = list.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            IdType midId = idGetter.apply(list.get(mid));

            int comparison = midId.compareTo(targetId);
            if (comparison == 0) {
                return list.get(mid);
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null; // Not found
    }
}
