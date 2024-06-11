package com.ogawa.fico.supplyingIterator;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;

public class OptionalUnwrappingSupplierWrapper<E> implements Supplier<E> {

    private final Supplier<Optional<E>> optionalSupplier;

    OptionalUnwrappingSupplierWrapper(@NonNull Supplier<Optional<E>> optionalSupplier) {
        this.optionalSupplier = optionalSupplier;
    }

    @Override
    public E get() {
        Optional<E> optional = optionalSupplier.get();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }


}
