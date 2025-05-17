package com.clothingstore.service;

import org.springframework.stereotype.Service;

@Service
public class RequestCounterService {

    private int requestCount = 0;

    public synchronized void increment() {
        requestCount++;
    }

    public synchronized int getCount() {
        return requestCount;
    }

    public synchronized void reset() {
        requestCount = 0;
    }
}
