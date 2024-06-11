package com.ogawa.fico.db;

import java.util.concurrent.atomic.AtomicLong;

public class Sequence {

    private AtomicLong sequence;

    Sequence(AtomicLong sequence) {
        this.sequence = sequence;
    }

    public long current() {
        return sequence.get();
    }

    public long next() {
        return sequence.incrementAndGet();
    }

}
