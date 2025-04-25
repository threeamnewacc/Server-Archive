// Decompiled with: CFR 0.152
// Class Version: 8
package us.zonix.practice.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapSorting {
    private static final Function EXTRACT_KEY = new Function<Map.Entry<Object, Object>, Object>(){

        @Override
        public Object apply(Map.Entry<Object, Object> input) {
            return input == null ? null : input.getKey();
        }
    };
    private static final Function EXTRACT_VALUE = new Function<Map.Entry<Object, Object>, Object>(){

        @Override
        public Object apply(Map.Entry<Object, Object> input) {
            return input == null ? null : input.getValue();
        }
    };

    public static <T, V extends Comparable<V>> List<Map.Entry<T, V>> sortedValues(Map<T, V> map) {
        return MapSorting.sortedValues(map, Ordering.natural());
    }

    public static <T, V> List<Map.Entry<T, V>> sortedValues(Map<T, V> map, Comparator<V> valueComparator) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(valueComparator))
                .collect(Collectors.toList());
    }

    public static <T, V> Iterable<T> keys(List<Map.Entry<T, V>> entryList) {
        return Iterables.transform(entryList, MapSorting.extractKey());
    }

    public static <T, V> Iterable<V> values(List<Map.Entry<T, V>> entryList) {
        return Iterables.transform(entryList, MapSorting.extractValue());
    }

    private static <T, V> Function<Map.Entry<T, V>, T> extractKey() {
        return EXTRACT_KEY;
    }

    private static <T, V> Function<Map.Entry<T, V>, V> extractValue() {
        return EXTRACT_VALUE;
    }
}
