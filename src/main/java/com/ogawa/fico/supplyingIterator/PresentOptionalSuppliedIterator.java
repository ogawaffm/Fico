package com.ogawa.fico.supplyingIterator;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * Iterator that supplies non-null values from a supplier. The iteration ends when the supplier returns an empty
 * Optional or the iteration limit is reached.
 *
 * @param <E> Type of the supplied values
 */
public class PresentOptionalSuppliedIterator<E> extends NonNullSuppliedIterator<E> {

    /**
     * Creates a new PresentOptionalSuppliedIterator with the given Optional supplier and iteration limit.
     *
     * @param supplier       Supplier of Optional containing the values
     * @param iterationLimit Maximum number of iterations. If 0, the iteration is infinite.
     */
    public PresentOptionalSuppliedIterator(@NonNull Supplier<Optional<E>> supplier, int iterationLimit) {
        super(new OptionalUnwrappingSupplierWrapper<>(supplier), iterationLimit);
    }

}
