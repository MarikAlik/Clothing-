package com.clothingstore.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private static final int MAX_CACHE_SIZE = 100;

    private final Map<String, Object> cache;

    public CacheService() {

        this.cache = new LinkedHashMap<String, Object>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {

                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public void clear() {
        cache.clear();
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}
