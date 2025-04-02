package com.clothingstore.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private static final int MAX_CACHE_SIZE = 100; // Максимальный размер кэша

    // Используем LinkedHashMap для реализации кэша с ограничением размера
    private final Map<String, Object> cache;

    public CacheService() {
        // LinkedHashMap сохраняет порядок элементов, что позволяет нам легко удалять старые элементы при переполнении
        this.cache = new LinkedHashMap<String, Object>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                // Удаляем старейший элемент, если кэш превышает MAX_CACHE_SIZE
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    // Метод для добавления данных в кэш
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    // Метод для получения данных из кэша
    public Object get(String key) {
        return cache.get(key);
    }

    // Метод для очистки кэша
    public void clear() {
        cache.clear();
    }

    // Метод для проверки наличия данных в кэше
    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}
