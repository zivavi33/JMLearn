package net.acimon.jmlearn.utils;

import java.util.HashMap;
import java.util.Map;


public class Counter {

    private Map<Integer, Integer> _Counter;

    public Counter(int[] collection) {
        if (collection.length ==0) {
            throw new IllegalStateException("Cannot find most common element in an empty collection.");
        }
        _Counter = new HashMap<>();
        for (int label : collection) {
            _Counter.put(label, _Counter.getOrDefault(label, 0) + 1);
        }
    }
    public int mostCommon(){
        return _Counter.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }
}
