package com.ogawa.fico.supplyingIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * Iterator that supplies non-null values from a supplier. The iteration ends when the supplier returns null or the
 * iteration limit is reached.
 *
 * @param <E> Type of the supplied values
 */
public class NonNullSuppliedIterator<E> implements Iterator<E> {

    protected final Supplier<E> supplier;
    private E valueBuffer;

    final private long iterationLimit;
    private long iterationNo = 0;

    /**
     * Creates a new NonNullSuppliedIterator with the given supplier and iteration limit.
     *
     * @param supplier       Supplier of the values
     * @param iterationLimit Maximum number of iterations. If 0, the iteration is infinite.
     */
    public NonNullSuppliedIterator(@NonNull Supplier<E> supplier, long iterationLimit) {

        if (iterationLimit < 0) {
            throw new IllegalArgumentException("iterationLimit must be >= 0");
        }

        this.supplier = supplier;
        this.iterationLimit = iterationLimit == 0L ? -1L : iterationLimit;
    }

    /**
     * Returns the iteration limit. If 0, the iteration is infinite.
     *
     * @return iteration limit
     */
    public long getIterationLimit() {
        return iterationLimit == -1L ? 0L : iterationLimit;
    }

    /**
     * Returns the number of successfully performed iterations.
     *
     * @return number of iterations or 0 if no iteration has been performed yet
     */
    public long getIterationNo() {
        return iterationNo;
    }

    @Override
    public boolean hasNext() {

        // Just read last E?
        if (iterationNo == iterationLimit) {
            return false;
        } else {
            // Very first call of hasNext()?
            if (valueBuffer == null) {
                // yes, read ahead
                valueBuffer = supplier.get();
            }
        }
        return valueBuffer != null;
    }

    @Override
    public E next() {

        // No next row available?
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        iterationNo++;

        E returnValue = valueBuffer;

        // read ahead
        valueBuffer = supplier.get();

        return returnValue;

    }

}
