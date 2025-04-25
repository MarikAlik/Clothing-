package com.clothingstore.service;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class VisitCountService {
    private final AtomicInteger visitCount = new AtomicInteger(0);

    public void incrementVisitCount() {
        visitCount.incrementAndGet();
    }

    public int getVisitCount() {
        return visitCount.get();
    }
}
