package com.ogawa.fico.supplyingIterator;

import java.util.Iterator;
import lombok.NonNull;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Transferor<T> implements Callable<Integer> {

    private int transferCount = 0;
    Iterator<T> iterator;
    Consumer<T> consumer;

    public Transferor(@NonNull final Supplier<T> supplier, @NonNull final Consumer<T> consumer) {
        this(new NonNullSuppliedIterator<>(supplier, 0), consumer);
    }

    public Transferor(@NonNull final Iterable<T> reader, @NonNull final Consumer<T> consumer) {
        this(reader.iterator(), consumer);
    }

    public Transferor(@NonNull final Iterator<T> reader, @NonNull final Consumer<T> consumer) {
        this.iterator = reader;
        this.consumer = consumer;
    }

    public int getTransferCount() {
        return transferCount;
    }

    @Override
    public Integer call() {

        while (iterator.hasNext()) {
            transferCount++;
            consumer.accept(iterator.next());
        }
        return transferCount;

    }

}
