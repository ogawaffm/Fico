package com.ogawa.fico.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R> {

    R apply(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9);

    /**
     * Implementation following idea of {@link java.util.function.BiFunction#andThen(java.util.function.Function)}
     */
    default <R2> Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R2> andThen(
        Function<? super R, ? extends R2> after) {
        Objects.requireNonNull(after);
        return (A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9)
            -> after.apply(apply(a1, a2, a3, a4, a5, a6, a7, a8, a9));
    }

}
